package pl.agh.edu.to.neuronpicture.webcrawler.analyzer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.stream.Stream;

/**
 * Created by Sebastian on 2016-12-30.
 */

public abstract class AbstractPageContentAnalyzer implements PageContentAnalyzer {

    private final String query;

    public AbstractPageContentAnalyzer(String query) {
        this.query = query;
    }

    public Stream<PageAddress.Builder> analyze(Document document) {
        return doAnalyze(document.select(query).stream());
    }

    protected abstract Stream<PageAddress.Builder> doAnalyze(Stream<Element> elements);
}
