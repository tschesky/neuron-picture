package pl.agh.edu.to.neuronpicture.webcrawler.analyzer;

import org.asynchttpclient.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.discovered.Discovered;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.discovered.SetDiscoveredImpl;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.frontier.CrawlerFrontier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by Sebastian on 2017-01-03.
 */

public class ContentAnalyzer {
    public static final Logger LOGGER = LoggerFactory.getLogger(ContentAnalyzer.class);

    public static class Builder {
        private final CrawlerFrontier frontier;
        private PageContentAnalyzer urlAnalyzer = new UrlAnalyzer();
        private PageContentAnalyzer imgAnalyzer = new ImgAnalyzer();
        private Discovered discovered = new SetDiscoveredImpl();
        private int maxDepth = Integer.MAX_VALUE;

        public Builder(CrawlerFrontier frontier) {
            this.frontier = frontier;
        }

        public Builder urlAnalyzer(PageContentAnalyzer urlAnalyzer) {
            this.urlAnalyzer = urlAnalyzer;
            return this;
        }

        public Builder imgAnalyzer(PageContentAnalyzer imgAnalyzer) {
            this.imgAnalyzer = imgAnalyzer;
            return this;
        }

        public Builder discovered(Discovered discovered) {
            this.discovered = discovered;
            return this;
        }

        public Builder maxDepth(int depth) {
            this.maxDepth = depth;
            return this;
        }

        public ContentAnalyzer build() {
            return new ContentAnalyzer(this);
        }
    }

    private final PageContentAnalyzer urlAnalyzer;
    private final PageContentAnalyzer imgAnalyzer;
    private final CrawlerFrontier frontier;
    private final Discovered discovered;
    private final int maxDepth;
    private final AtomicBoolean depthReached = new AtomicBoolean(false);

    private ContentAnalyzer(Builder builder) {
        this.urlAnalyzer = builder.urlAnalyzer;
        this.imgAnalyzer = builder.imgAnalyzer;
        this.frontier = builder.frontier;
        this.discovered = builder.discovered;
        this.maxDepth = builder.maxDepth;
    }

    public CompletableFuture<CrawlerState> analyze(Response response, PageAddress source) {
        String url = response.getUri().toUrl();
        LOGGER.trace("Analyzing: {}", url);
        if (source.getDepth() == maxDepth) {
            depthReached.set(true);
        }
        try {
            Document document = Jsoup.parse(response.getResponseBody());
            if (source.getDepth() < maxDepth) {
                frontier.submitPages(doAnalyze(urlAnalyzer, document, source));
            }
            frontier.submitImages(doAnalyze(imgAnalyzer, document, source));
        } catch (Exception e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Error occurred during html parsing: {}", url, e);
            } else {
                LOGGER.info("Error occurred during html parsing: {}: {}", url, e.getMessage());
            }
            return CompletableFuture.completedFuture(CrawlerState.ERROR);
        }
        if (depthReached.get()) {
            return CompletableFuture.completedFuture(CrawlerState.RUNNING_WITH_DEPTH_REACHED);
        } else {
            return CompletableFuture.completedFuture(CrawlerState.RUNNING);
        }
    }

    private List<PageAddress> doAnalyze(PageContentAnalyzer analyzer, Document document, PageAddress source) {
        return analyzer.analyze(document)
                .map(builder -> builder.depth(source.getDepth() + 1))
                .map(PageAddress.Builder::build)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(page -> discovered.checkAndSave(page.getUrlString()))
                .collect(Collectors.toList());
    }
}
