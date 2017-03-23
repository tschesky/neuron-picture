package pl.agh.edu.to.neuronpicture.webcrawler.persistance;

import org.asynchttpclient.Response;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Sebastian on 2016-11-29.
 */

public interface ImagePersistenceManager {
    CompletableFuture<CrawlerState> save(Response response, PageAddress sourcePage);
    void dispose();
}
