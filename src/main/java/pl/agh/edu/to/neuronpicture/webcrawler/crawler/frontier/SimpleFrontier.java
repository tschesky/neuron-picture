package pl.agh.edu.to.neuronpicture.webcrawler.crawler.frontier;

import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;
import pl.agh.edu.to.neuronpicture.webcrawler.utils.Utils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Sebastian on 2016-12-18.
 */

public class SimpleFrontier implements Frontier {
    private final Queue<PageAddress> frontier;
    private final AtomicBoolean working = new AtomicBoolean(false);

    public SimpleFrontier() {
        this.frontier = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void submitURLs(List<PageAddress> urls) {
        frontier.addAll(urls);
    }

    @Override
    public CompletableFuture<CrawlerState> schedule(ScheduleCommand command) {
        if (working.compareAndSet(false, true)) {
            return command.apply(frontier).thenApply((s -> Utils.setup(s, v -> working.set(false))));
        } else {
            return CompletableFuture.completedFuture(CrawlerState.RUNNING);
        }
    }

    @Override
    public boolean isWorking() {
        return working.get();
    }

    @Override
    public boolean canWork() {
        return !frontier.isEmpty() && !isWorking();
    }
}
