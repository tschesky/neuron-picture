package pl.agh.edu.to.neuronpicture.webcrawler.downloader;

import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Sebastian on 2017-01-02.
 */

public interface Downloader {
    CompletableFuture<CrawlerState> fetchURL(DownloadCommand command);
    void dispose() throws IOException;
}
