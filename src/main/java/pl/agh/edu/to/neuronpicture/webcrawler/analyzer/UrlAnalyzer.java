package pl.agh.edu.to.neuronpicture.webcrawler.analyzer;

import com.google.common.collect.Lists;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.nodes.Element;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Sebastian on 2016-12-30.
 */

public class UrlAnalyzer extends AbstractPageContentAnalyzer {

    private static final String LINK_QUERY = "a[href]";
    private static final UrlValidator VALIDATOR = new UrlValidator(Arrays.asList("http", "https").toArray(new String[0])); // uhh!
    private final List<String> forbidden = Lists.newArrayList(".pdf", ".xlsx", ".doc", ".docx", ".odt", ".pptx", ".jpg",
            ".jpeg", ".png", ".gif");

    public UrlAnalyzer() {
        super(LINK_QUERY);
    }

    public UrlAnalyzer(String query) {
        super(query);
    }

    @Override
    protected Stream<PageAddress.Builder> doAnalyze(Stream<Element> elements) {
        return elements
                .map(e -> e.attr("abs:href"))
                .filter(VALIDATOR::isValid)
                .filter(url -> forbidden.stream().noneMatch(url::endsWith))
                .map(url -> new PageAddress.Builder().url(url));
    }

}
