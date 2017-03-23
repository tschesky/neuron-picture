package pl.agh.edu.to.neuronpicture.webcrawler.persistance;

import com.google.common.base.Preconditions;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;

import java.awt.image.BufferedImage;

/**
 * Created by Sebastian on 2016-11-29.
 */

public class DownloadedImage {
    private final PageAddress sourcePage;
    private final BufferedImage image;

    public DownloadedImage(PageAddress sourcePage, BufferedImage image) {
        Preconditions.checkNotNull(image);
        this.sourcePage = sourcePage;
        this.image = image;
    }

    public PageAddress getSourcePage() {
        return sourcePage;
    }

    public BufferedImage getImage() {
        return image;
    }
}
