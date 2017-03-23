package pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image;

import com.google.common.base.Preconditions;
import org.jsoup.nodes.Element;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.DownloadedImage;

/**
 * Created by Sebastian on 2016-12-30.
 */

public class ImageSizeFilter implements ImageFilter {

    private final int minWidth;
    private final int minHeight;
    private final boolean pass; // if cannot check

    public ImageSizeFilter(int minWidth, int minHeight) {
        this(minWidth, minHeight, true);
    }

    public ImageSizeFilter(int minWidth, int minHeight, boolean pass) {
        Preconditions.checkArgument(minWidth > 0 && minHeight > 0, "Image dimensions must be positive");
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.pass = pass;
    }

    @Override
    public boolean test(Element element) {
        try {
            int width = Integer.parseInt(element.attr("width"));
            int height = Integer.parseInt(element.attr("height"));
            return width >= minWidth && height >= minHeight;
        } catch (NumberFormatException e) {
            // attribute not present or malformed - nothing to do here
        }
        return pass;
    }

    @Override
    public boolean test(DownloadedImage image) {
        return image.getImage().getWidth() >= minWidth && image.getImage().getHeight() >= minHeight;
    }
}
