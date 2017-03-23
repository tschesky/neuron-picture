package pl.agh.edu.to.neuronpicture.webcrawler.crawler;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Sebastian on 2016-11-29.
 */

public interface Crawler {
    CompletableFuture<CrawlingResult> crawl();
    void dispose();
}
