package pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image;

import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.Filter;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.DownloadedImage;

/**
 * Created by Sebastian on 2017-01-07.
 */

public interface ImageFilter extends Filter {
    boolean test(DownloadedImage image);
}
