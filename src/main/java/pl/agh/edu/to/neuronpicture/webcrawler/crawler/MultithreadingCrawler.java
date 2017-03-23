package pl.agh.edu.to.neuronpicture.webcrawler.crawler;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.ContentAnalyzer;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.FilteringImgAnalyzer;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.discovered.SetDiscoveredImpl;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image.ImageFilter;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image.ImageVerifier;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.frontier.CrawlerFrontier;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.frontier.ScheduleCommand;
import pl.agh.edu.to.neuronpicture.webcrawler.downloader.DefaultDownloader;
import pl.agh.edu.to.neuronpicture.webcrawler.downloader.DownloadCommand;
import pl.agh.edu.to.neuronpicture.webcrawler.downloader.DownloadCommandFactory;
import pl.agh.edu.to.neuronpicture.webcrawler.downloader.Downloader;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.ImagePersistenceManager;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.ImagePersistenceManagerImpl;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.strategy.ImagePersistenceStrategy;
import pl.agh.edu.to.neuronpicture.webcrawler.utils.HttpClientFactory;
import pl.agh.edu.to.neuronpicture.webcrawler.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Sebastian on 2016-12-18.
 */

public class MultithreadingCrawler implements Crawler {
    public static final Logger LOGGER = LoggerFactory.getLogger(MultithreadingCrawler.class);

    public static class Builder {
        private final ImagePersistenceStrategy strategy;
        private final List<URL> seed;
        private int concurrencyLevel = 64;
        private int imageCount = Integer.MAX_VALUE;
        private int maxDepth = Integer.MAX_VALUE;
        private Duration timeout = Duration.ofMinutes(5);
        private ImageVerifier imageVerifier = new ImageVerifier(Collections.emptyList());

        public Builder(ImagePersistenceStrategy strategy, URL... seed) {
            this(strategy, Arrays.asList(seed));
        }

        public Builder(ImagePersistenceStrategy strategy, List<URL> seed) {
            Preconditions.checkArgument(!seed.isEmpty(), "Seed url list cannot be empty");
            this.strategy = strategy;
            this.seed = seed;
        }

        public Builder concurrencyLevel(int level) {
            this.concurrencyLevel = level;
            return this;
        }

        public Builder imageCount(int count) {
            this.imageCount = count;
            return this;
        }

        public Builder maxDepth(int depth) {
            this.maxDepth = depth;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder imageFilters(ImageFilter... filters) {
            this.imageVerifier = new ImageVerifier(filters);
            return this;
        }

        public MultithreadingCrawler build() {
            return new MultithreadingCrawler(this);
        }
    }

    private final CrawlerFrontier frontier;
    private final Downloader downloader;
    private final DownloadCommandFactory factory;
    private final Duration timeout;

    private final CompletableFuture<CrawlingStatus> endConditionReached = new CompletableFuture<>();

    private final ScheduledThreadPoolExecutor coordinator =
            Utils.setup(new ScheduledThreadPoolExecutor(1),
                    e -> e.setExecuteExistingDelayedTasksAfterShutdownPolicy(false));

    private MultithreadingCrawler(Builder builder) {
        this.frontier = CrawlerFrontier.create(builder.concurrencyLevel);
        this.downloader = new DefaultDownloader(HttpClientFactory.getThrottledClient(2 * builder.concurrencyLevel));

        ContentAnalyzer analyzer = new ContentAnalyzer.Builder(frontier)
                .discovered(new SetDiscoveredImpl(Utils.URLsToStrings(builder.seed)))
                .imgAnalyzer(new FilteringImgAnalyzer(builder.imageVerifier))
                .maxDepth(builder.maxDepth)
                .build();
        ImagePersistenceManager persistenceManager = new ImagePersistenceManagerImpl(builder.strategy, builder.imageVerifier, builder.imageCount);
        this.factory = DownloadCommandFactory.createFactory(analyzer, persistenceManager);
        this.timeout = builder.timeout;

        frontier.submitPages(builder.seed.stream().map(PageAddress::new).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<CrawlingResult> crawl() {
        CompletableFuture<CrawlingResult> crawlingFinished = scheduleUrlPolling().thenApply(status -> {
            LOGGER.info("Crawling finished with status {}", status);
            return new CrawlingResult(status);
        });
        scheduleTimeout(endConditionReached);
        return crawlingFinished;
    }

    private CompletableFuture<CrawlerState> fetchNext(Function<PageAddress, DownloadCommand> commandCreator, Queue<PageAddress> urls) {
        if (urls.isEmpty() || endConditionReached.isDone()) {
            LOGGER.trace("Fetching URL batch finished");
            return CompletableFuture.completedFuture(CrawlerState.BATCH_FINISHED);
        } else {
            return downloader.fetchURL(commandCreator.apply(urls.poll()))
                    .thenCompose(status -> {
                        if (status != CrawlerState.RUNNING_WITH_LIMIT_REACHED) {
                            return fetchNext(commandCreator, urls).thenApply(status::combine);
                        } else {
                            return CompletableFuture.completedFuture(status);
                        }
                    });
        }
    }

    private ScheduleCommand pageCommand(Function<PageAddress, DownloadCommand> commandCreator) {
        return new ScheduleCommand(urls -> fetchNext(commandCreator, urls));
    }

    private ScheduleCommand imageCommand(Function<PageAddress, DownloadCommand> commandCreator) {
        return new ScheduleCommand(urls -> fetchNext(commandCreator, urls), status -> {
            if (status == CrawlerState.RUNNING_WITH_LIMIT_REACHED) {
                endConditionReached.complete(CrawlingStatus.LIMIT_REACHED);
            }
        });
    }

    private CompletableFuture<CrawlingStatus> scheduleUrlPolling() {
        CompletableFuture<CrawlingStatus> crawlingFinished = new CompletableFuture<>();
        coordinator.scheduleAtFixedRate(new Runnable() {
            private CompletableFuture<Void> finished = CompletableFuture.completedFuture(null);
            private CompletableFuture<CrawlerState> pageFuture = CompletableFuture.completedFuture(CrawlerState.RUNNING);
            private CompletableFuture<CrawlerState> imageFuture = CompletableFuture.completedFuture(CrawlerState.RUNNING);

            @Override
            public void run() {
                if (!endConditionReached.isDone()) {
                    LOGGER.debug("Poll");
                    this.pageFuture = pageFuture
                            .thenCombine(frontier.schedulePages(MultithreadingCrawler.this.pageCommand(factory::createPage)),
                                    CrawlerState::combine);
                    this.imageFuture = imageFuture.
                            thenCombine(frontier.scheduleImages(MultithreadingCrawler.this.imageCommand(factory::createImage)),
                                    CrawlerState::combine);
                    this.finished = finished.thenCompose(v ->  pageFuture.thenCombine(imageFuture, this::combineResults));
                } else {
                    finished.thenCompose(v -> endConditionReached).thenApply(crawlingFinished::complete);
                }
            }

            private Void combineResults(CrawlerState pageStatus, CrawlerState imageStatus) {
                if (pageStatus == CrawlerState.NOT_RUNNING && imageStatus == CrawlerState.NOT_RUNNING) {
                    endConditionReached.complete(CrawlingStatus.DEAD);
                } else if (pageStatus == CrawlerState.NOT_RUNNING_WITH_DEPTH_REACHED && imageStatus == CrawlerState.NOT_RUNNING) {
                    endConditionReached.complete(CrawlingStatus.DEPTH_REACHED);
                }
                return null;
            }

        } , 0, 1, TimeUnit.SECONDS);

        return crawlingFinished;
    }

    private void scheduleTimeout(CompletableFuture<CrawlingStatus> endConditionReached) {
        coordinator.schedule(() -> endConditionReached.complete(CrawlingStatus.TIMEOUT), timeout.getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void dispose() {
        coordinator.shutdown();
        try {
            downloader.dispose();
            factory.dispose();
        } catch (IOException e) {
            LOGGER.error("Error occurred during crawler shutdown", e);
        }
    }
}
