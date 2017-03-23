package pl.agh.edu.to.neuronpicture.webcrawler.persistance;

import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image.ImageFilter;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.strategy.ImagePersistenceStrategy;
import pl.agh.edu.to.neuronpicture.webcrawler.utils.Utils;

import javax.imageio.ImageIO;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sebastian on 2017-01-03.
 */

public class ImagePersistenceManagerImpl implements ImagePersistenceManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(ImagePersistenceManagerImpl.class);

    private final ImagePersistenceStrategy strategy;
    private final ImageFilter imageVerifier;
    private final AtomicInteger downloadedCount;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public ImagePersistenceManagerImpl(ImagePersistenceStrategy strategy, ImageFilter imageVerifier, int limit) {
        this.strategy = strategy;
        this.imageVerifier = imageVerifier;
        this.downloadedCount = new AtomicInteger(limit);
    }

    @Override
    public CompletableFuture<CrawlerState> save(Response response, PageAddress sourcePage) {
        return CompletableFuture
                .supplyAsync(response::getResponseBodyAsStream, executor)
                .thenApply(stream -> {
                    try {
                        return new DownloadedImage(sourcePage, ImageIO.read(stream));
                    } catch (Exception e) {
                        return null;
                    }
                })
                .thenApply(this::filter)
                .thenApply(this::count)
                .exceptionally(e -> Utils.toError(v -> LOGGER.info("Cannot read image", e)));
    }

    private CrawlerState count(Boolean success) {
        if (success && downloadedCount.decrementAndGet() <= 0) {
            return CrawlerState.RUNNING_WITH_LIMIT_REACHED;
        } else {
            return CrawlerState.RUNNING;
        }
    }

    private boolean filter(DownloadedImage image) {
        return image != null && imageVerifier.test(image) && strategy.save(image);
    }

    @Override
    public void dispose() {
        executor.shutdown();
    }
}
