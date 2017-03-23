package pl.agh.edu.to.neuronpicture.webcrawler.crawler.frontier;

import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;
import pl.agh.edu.to.neuronpicture.webcrawler.utils.Utils;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Sebastian on 2016-12-19.
 */

public class ScheduleCommand {
    private final Function<Queue<PageAddress>, CompletableFuture<CrawlerState>> function;
    private final Consumer<CrawlerState> callback;

    public ScheduleCommand(Function<Queue<PageAddress>, CompletableFuture<CrawlerState>> function, Consumer<CrawlerState> callback) {
        this.function = function;
        this.callback = callback;
    }

    public ScheduleCommand(Function<Queue<PageAddress>, CompletableFuture<CrawlerState>> function) {
        this(function, s -> {});
    }

    public CompletableFuture<CrawlerState> apply(Queue<PageAddress> urls) {
        return function.apply(urls).thenApply(s -> Utils.setup(s, callback));
    }
}
