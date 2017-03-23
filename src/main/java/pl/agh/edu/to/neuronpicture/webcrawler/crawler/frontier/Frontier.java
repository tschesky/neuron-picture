package pl.agh.edu.to.neuronpicture.webcrawler.crawler.frontier;

import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Sebastian on 2016-12-18.
 */

public interface Frontier {
    void submitURLs(List<PageAddress> urls);
    CompletableFuture<CrawlerState> schedule(ScheduleCommand command);
    boolean isWorking();
    boolean canWork();
}
