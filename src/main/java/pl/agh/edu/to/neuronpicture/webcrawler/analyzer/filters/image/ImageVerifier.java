package pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image;

import org.jsoup.nodes.Element;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.DownloadedImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sebastian on 2017-01-09.
 */

public class ImageVerifier implements ImageFilter {
    private final List<ImageFilter> filters;

    public ImageVerifier(ImageFilter... filters) {
        this(Arrays.asList(filters));
    }

    public ImageVerifier(List<ImageFilter> filters) {
        this.filters = new ArrayList<>(filters);
    }

    @Override
    public boolean test(DownloadedImage image) {
        return filters.stream().allMatch(f -> f.test(image));
    }

    @Override
    public boolean test(Element element) {
        return filters.stream().allMatch(f -> f.test(element));
    }
}
