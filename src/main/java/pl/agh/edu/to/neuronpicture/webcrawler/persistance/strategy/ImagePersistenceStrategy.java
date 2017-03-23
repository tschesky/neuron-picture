package pl.agh.edu.to.neuronpicture.webcrawler.persistance.strategy;

import pl.agh.edu.to.neuronpicture.webcrawler.persistance.DownloadedImage;

/**
 * Created by Sebastian on 2017-01-03.
 */

public interface ImagePersistenceStrategy {
    boolean save(DownloadedImage image);
}
