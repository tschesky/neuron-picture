package pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image;

import com.google.common.base.Preconditions;
import org.jsoup.nodes.Element;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.DownloadedImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sebastian on 2017-01-01.
 */

public class ImageFormatFilter implements ImageFilter {

    private final List<String> allowedExtensions;

    public ImageFormatFilter(List<String> allowedExtensions) {
        Preconditions.checkArgument(!allowedExtensions.isEmpty(), "Extension list cannot be empty");
        this.allowedExtensions = new ArrayList<>(allowedExtensions);
    }

    public ImageFormatFilter(String... allowedExtensions) {
        this(Arrays.asList(allowedExtensions));
    }

    @Override
    public boolean test(Element element) {
        String src = element.attr("src");
        return allowedExtensions.stream().anyMatch(src::endsWith);
    }

    @Override
    public boolean test(DownloadedImage image) {
        return true;
    }
}
