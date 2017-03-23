package pl.agh.edu.to.neuronpicture.webcrawler.persistance.strategy;

import pl.agh.edu.to.neuronpicture.webcrawler.persistance.DownloadedImage;

import java.nio.file.Path;

/**
 * Created by Sebastian on 2017-01-09.
 */

public class HierarchicalPersistenceStrategy implements ImagePersistenceStrategy {

    private final Path rootDir;

    public HierarchicalPersistenceStrategy(Path rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public boolean save(DownloadedImage image) {
        //todo implement
        return false;
    }
}
