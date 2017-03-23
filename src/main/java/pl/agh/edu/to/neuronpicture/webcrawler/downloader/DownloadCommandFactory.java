package pl.agh.edu.to.neuronpicture.webcrawler.downloader;

import pl.agh.edu.to.neuronpicture.webcrawler.analyzer.ContentAnalyzer;
import pl.agh.edu.to.neuronpicture.webcrawler.crawler.PageAddress;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.ImagePersistenceManager;

/**
 * Created by Sebastian on 2017-01-03.
 */

public class DownloadCommandFactory {

    private final ContentAnalyzer analyzer;
    private final ImagePersistenceManager manager;

    private DownloadCommandFactory(ContentAnalyzer analyzer, ImagePersistenceManager manager) {
        this.analyzer = analyzer;
        this.manager = manager;
    }

    public DownloadCommand createImage(PageAddress image) {
        return new ImageDownloadCommand(manager, image);
    }

    public DownloadCommand createPage(PageAddress pageAddress) {
        return new PageDownloadCommand(analyzer, pageAddress);
    }

    public void dispose() {
        manager.dispose();
    }

    public static DownloadCommandFactory createFactory(ContentAnalyzer analyzer, ImagePersistenceManager manager) {
        return new DownloadCommandFactory(analyzer, manager);
    }
}
