package pl.agh.edu.to.neuronpicture.webcrawler.crawler.frontier;

import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Sebastian on 2017-01-03.
 */

public class CrawlerFrontier {
    private final Frontier pageFrontier;
    private final Frontier imageFrontier;

    public CrawlerFrontier(Frontier pageFrontier, Frontier imageFrontier) {
        this.pageFrontier = pageFrontier;
        this.imageFrontier = imageFrontier;
    }

    public void submitPages(List<PageAddress> pages) {
        pageFrontier.submitURLs(pages);
    }

    public void submitImages(List<PageAddress> images) {
        imageFrontier.submitURLs(images);
    }

    public CompletableFuture<CrawlerState> schedulePages(ScheduleCommand command) {
        return pageFrontier.schedule(command);
    }

    public CompletableFuture<CrawlerState> scheduleImages(ScheduleCommand command) {
        return imageFrontier.schedule(command);
    }

    public static CrawlerFrontier create(int concurrencyLevel) {
        int level = (concurrencyLevel >= 4) ? concurrencyLevel / 4 : 1;
        return new CrawlerFrontier(new ConcurrentFrontier(concurrencyLevel), new ConcurrentFrontier(level));
    }
}
