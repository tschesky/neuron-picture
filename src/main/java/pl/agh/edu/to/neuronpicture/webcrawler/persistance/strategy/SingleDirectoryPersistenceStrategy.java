package pl.agh.edu.to.neuronpicture.webcrawler.persistance.strategy;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.DownloadedImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Sebastian on 2016-11-29.
 */

public class SingleDirectoryPersistenceStrategy implements ImagePersistenceStrategy {
    public static final Logger LOGGER = LoggerFactory.getLogger(SingleDirectoryPersistenceStrategy.class);

    private final Path dir;

    public SingleDirectoryPersistenceStrategy() {
        this(Paths.get("").toAbsolutePath().resolve("images"));
    }

    public SingleDirectoryPersistenceStrategy(Path dir) {
        this(dir, true);
    }

    public SingleDirectoryPersistenceStrategy(Path dir, boolean create) {
        this.dir = dir.toAbsolutePath();
        Preconditions.checkArgument(create || Files.isDirectory(this.dir), "Directory %s doesn't exists", this.dir);
        if (!Files.isDirectory(this.dir)) {
            try {
                Files.createDirectory(this.dir);
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot create directory", e);
            }
        }
    }

    @Override
    public boolean save(DownloadedImage image) {
        int index = image.getSourcePage().getUrlString().lastIndexOf('/');
        String filename = image.getSourcePage().getUrlString().substring(index + 1);
        Path path = dir.resolve(filename);
        String format = filename.substring(filename.lastIndexOf('.') + 1);
        return doSave(image.getImage(), path, format);
    }

    private boolean doSave(BufferedImage image, Path path, String format) {
        try {
            ImageIO.write(image, format, path.toFile());
            LOGGER.debug("Saved {}", path.toString());
            return true;
        } catch (IOException e) {
            LOGGER.warn("Cannot save image {}", path, e);
        }
        return false;
    }
}
