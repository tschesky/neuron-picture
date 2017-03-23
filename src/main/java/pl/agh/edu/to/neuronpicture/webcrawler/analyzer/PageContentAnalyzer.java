package pl.agh.edu.to.neuronpicture.webcrawler.analyzer;

import org.jsoup.nodes.Document;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.stream.Stream;

/**
 * Created by Sebastian on 2016-11-29.
 */

public interface PageContentAnalyzer {
    Stream<PageAddress.Builder> analyze(Document document);
}
