package pl.agh.edu.to.neuronpicture.webcrawler.crawler.frontier;

import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Sebastian on 2016-12-18.
 */

public class ConcurrentFrontier implements Frontier {
    private final List<Frontier> frontiers;
    private final int concurrencyLevel;

    public ConcurrentFrontier(int concurrencyLevel) {
        this.frontiers = IntStream.range(0, concurrencyLevel)
                .mapToObj(i -> new SimpleFrontier())
                .collect(Collectors.toList());
        this.concurrencyLevel = concurrencyLevel;
    }

    @Override
    public void submitURLs(List<PageAddress> urls) {
        urls.stream()
                .collect(Collectors.groupingBy(this::affinity))
                .forEach((id, list) -> frontiers.get(id).submitURLs(list));
    }

    @Override
    public CompletableFuture<CrawlerState> schedule(ScheduleCommand command) {
        if (frontiers.stream().allMatch(Frontier::isWorking)) {
            return CompletableFuture.completedFuture(CrawlerState.RUNNING);
        } else {
            return frontiers.stream()
                    .filter(Frontier::canWork)
                    .map(f -> f.schedule(command))
                    .collect(Collectors.toList())
                    .stream()
                    .reduce(CompletableFuture.completedFuture(CrawlerState.NOT_RUNNING),
                            (f1, f2) -> f1.thenCombine(f2, CrawlerState::combine));

        }
    }

    @Override
    public boolean isWorking() {
        return frontiers.stream().anyMatch(Frontier::isWorking);
    }

    @Override
    public boolean canWork() {
        return frontiers.stream().anyMatch(Frontier::canWork);
    }

    private int affinity(PageAddress page) {
        return Math.abs(page.getUrl().getHost().hashCode()) % getConcurrencyLevel();
    }

    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }
}
