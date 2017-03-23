package pl.agh.edu.to.neuronpicture.webcrawler.crawler;

import java.util.concurrent.CompletableFuture;

/**
 * Created by sebastian on 16/12/16.
 */

public class MockCrawler implements Crawler {
    @Override
    public CompletableFuture<CrawlingResult> crawl() {
        return CompletableFuture.completedFuture(new CrawlingResult(CrawlingStatus.TIMEOUT));
    }

    @Override
    public void dispose() {
        // do nothing
    }
}
