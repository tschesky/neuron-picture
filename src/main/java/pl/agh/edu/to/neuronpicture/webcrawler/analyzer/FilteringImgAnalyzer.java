package pl.agh.edu.to.neuronpicture.webcrawler.analyzer;

import org.jsoup.nodes.Element;
import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.Filter;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Sebastian on 2016-12-30.
 */

public class FilteringImgAnalyzer extends ImgAnalyzer {

    private static final String IMG_QUERY = "img";

    private final List<Filter> filters;

    public FilteringImgAnalyzer(Filter... filters) {
        this(Arrays.asList(filters));
    }

    public FilteringImgAnalyzer(List<Filter> filters) {
        super(IMG_QUERY);
        this.filters = new ArrayList<>(filters);
    }

    @Override
    protected Stream<PageAddress.Builder> doAnalyze(Stream<Element> elements) {
        return super.doAnalyze(elements.filter(elem -> filters.stream().allMatch(filter -> filter.test(elem))));
    }
}
