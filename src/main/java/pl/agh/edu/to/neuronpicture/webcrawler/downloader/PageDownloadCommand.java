package pl.agh.edu.to.neuronpicture.webcrawler.downloader;

import org.asynchttpclient.Response;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.ContentAnalyzer;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.concurrent.CompletableFuture;

/**
 * Created by Sebastian on 2017-01-03.
 */

public class PageDownloadCommand implements DownloadCommand {

    private final ContentAnalyzer analyzer;
    private final PageAddress page;

    public PageDownloadCommand(ContentAnalyzer analyzer, PageAddress page) {
        this.analyzer = analyzer;
        this.page = page;
    }

    @Override
    public CompletableFuture<CrawlerState> handleResponse(Response response) {
        return analyzer.analyze(response, page);
    }

    @Override
    public PageAddress getContent() {
        return page;
    }
}
