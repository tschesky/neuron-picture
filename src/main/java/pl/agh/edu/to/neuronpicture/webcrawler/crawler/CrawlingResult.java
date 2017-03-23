package pl.agh.edu.to.neuronpicture.webcrawler.crawler;

/**
 * Created by Sebastian on 2017-01-13.
 */

public class CrawlingResult {
    private final CrawlingStatus status;

    public CrawlingResult(CrawlingStatus status) {
        this.status = status;
    }

    public CrawlingStatus getStatus() {
        return status;
    }
}
