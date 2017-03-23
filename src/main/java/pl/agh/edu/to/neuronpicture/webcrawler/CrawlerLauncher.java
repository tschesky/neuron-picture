package pl.agh.edu.to.neuronpicture.webcrawler;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image.ImageFormatFilter;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image.ImageSizeFilter;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.Crawler;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.MultithreadingCrawler;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.strategy.SingleDirectoryPersistenceStrategy;
import pl.agh.edu.to.neuronpicture.webcrawler.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by sebastian on 26/11/16.
 */

public class CrawlerLauncher {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerLauncher.class);

    private CrawlerLauncher() {
    }

    public static void main(String[] args) throws IOException {
        try {
            Crawler crawler = getCrawler(Arrays.asList(args));
            crawler.crawl().join();
            crawler.dispose();
        } catch (Exception e) {
            LOGGER.error("Fatal error", e);
        }
    }

    private static List<URL> parseUrls(List<String> args) {
        return args.stream()
                .map(url -> Utils.toURLOpt(url, System.out::println))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private static Crawler getCrawler(List<String> args) {
        if (args.size() > 2 && args.get(0).equals("-m")) {
            int concurrencyLevel = Integer.parseInt(args.get(1));
            List<URL> urls = parseUrls(args.subList(2, args.size()));
            return new MultithreadingCrawler.Builder(new SingleDirectoryPersistenceStrategy(), urls)
                    .concurrencyLevel(concurrencyLevel)
                    .imageCount(15)
                    //.maxDepth(1)
                    .imageFilters(new ImageFormatFilter(Lists.newArrayList("png")), new ImageSizeFilter(400, 400))
                    .build();
        } else if (!args.isEmpty() && !args.get(0).equals("-m")) {
            List<URL> urls = parseUrls(args);
            return new MultithreadingCrawler.Builder(new SingleDirectoryPersistenceStrategy(), urls)
                    .concurrencyLevel(32)
                    .imageCount(5)
                    .build();
        } else {
            throw new IllegalArgumentException("Wrong arguments");
        }
    }
}
