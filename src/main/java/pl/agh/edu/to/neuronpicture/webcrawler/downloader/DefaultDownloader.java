package pl.agh.edu.to.neuronpicture.webcrawler.downloader;

import org.asynchttpclient.AsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.utils.HttpClientFactory;
import pl.agh.edu.to.neuronpicture.webcrawler.utils.Utils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sebastian on 2016-12-18.
 */

public class DefaultDownloader implements Downloader {
    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultDownloader.class);

    private final AsyncHttpClient httpClient;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public DefaultDownloader(AsyncHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public DefaultDownloader() {
        this(HttpClientFactory.getDefaultClient());
    }

    public CompletableFuture<CrawlerState> fetchURL(DownloadCommand command) {
        return httpClient.prepareGet(command.getContent().getUrlString()).execute()
                .toCompletableFuture()
                .thenComposeAsync(command::handleResponse, executor)
                .exceptionally(e -> Utils.toError(v -> LOGGER.info(
                        "Unsuccessful analysis of: {}: {}", command.getContent().getUrlString(), e.getMessage())));
    }

    public void dispose() throws IOException {
        httpClient.close();
        executor.shutdown();
    }
}
