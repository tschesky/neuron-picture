package pl.agh.edu.to.neuronpicture.webcrawler.downloader;

import org.asynchttpclient.Response;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.ImagePersistenceManager;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Sebastian on 2017-01-03.
 */

public class ImageDownloadCommand implements DownloadCommand {

    private final ImagePersistenceManager manager;
    private final PageAddress image;

    public ImageDownloadCommand(ImagePersistenceManager manager, PageAddress image) {
        this.manager = manager;
        this.image = image;
    }

    @Override
    public CompletableFuture<CrawlerState> handleResponse(Response response) {
        return manager.save(response, image);
    }

    public PageAddress getContent() {
        return this.image;
    }
}
