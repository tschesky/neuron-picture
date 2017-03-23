package pl.agh.edu.to.neuronpicture.webcrawler.crawler;

/**
 * Created by Sebastian on 2017-01-06.
 */

public enum CrawlerState {
    ERROR(-1),
    NOT_RUNNING(0),
    NOT_RUNNING_WITH_DEPTH_REACHED(1),
    RUNNING(0),
    RUNNING_WITH_DEPTH_REACHED(1),
    BATCH_FINISHED(0),
    BATCH_FINISHED_WITH_DEPTH_REACHED(1),
    RUNNING_WITH_LIMIT_REACHED(2);

    private int priority;

    CrawlerState(int priority) {
        this.priority = priority;
    }

    public CrawlerState combine(CrawlerState other) {
        if (this == RUNNING_WITH_DEPTH_REACHED && other == BATCH_FINISHED) {
            return BATCH_FINISHED_WITH_DEPTH_REACHED;
        } else if (this == BATCH_FINISHED_WITH_DEPTH_REACHED && other == NOT_RUNNING) {
            return NOT_RUNNING_WITH_DEPTH_REACHED;
        } else {
            if (this.priority <= other.priority) {
                return other;
            } else {
                return this;
            }
        }
    }

    public int getPriority() {
        return priority;
    }
}
