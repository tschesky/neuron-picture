package pl.agh.edu.to.neuronpicture.webcrawler.analyzer;

import org.jsoup.nodes.Element;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.stream.Stream;

/**
 * Created by Sebastian on 2016-12-30.
 */

public class ImgAnalyzer extends AbstractPageContentAnalyzer {

    private static final String IMG_QUERY = "img[src~=(?i)\\.(png|jpe?g|gif)]";

    public ImgAnalyzer() {
        super(IMG_QUERY);
    }

    public ImgAnalyzer(String query) {
        super(query);
    }

    @Override
    protected Stream<PageAddress.Builder> doAnalyze(Stream<Element> elements) {
        return elements
                .map(e -> e.attr("abs:src"))
                .map(url -> new PageAddress.Builder().url(url));
    }
}
