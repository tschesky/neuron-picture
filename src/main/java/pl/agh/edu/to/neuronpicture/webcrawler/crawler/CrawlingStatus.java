package pl.agh.edu.to.neuronpicture.webcrawler.crawler;

/**
 * Created by Sebastian on 2017-01-13.
 */

public enum CrawlingStatus {
    DEPTH_REACHED,
    LIMIT_REACHED,
    TIMEOUT,
    DEAD;
}
