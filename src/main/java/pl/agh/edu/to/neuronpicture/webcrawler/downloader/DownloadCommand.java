package pl.agh.edu.to.neuronpicture.webcrawler.downloader;

import org.asynchttpclient.Response;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Sebastian on 2017-01-03.
 */

public interface DownloadCommand {
    CompletableFuture<CrawlerState> handleResponse(Response response);
    PageAddress getContent();
}
