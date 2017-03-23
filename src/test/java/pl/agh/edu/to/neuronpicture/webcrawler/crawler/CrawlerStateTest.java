package pl.agh.edu.to.neuronpicture.webcrawler.crawler;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by Sebastian on 2017-01-13.
 */

public class CrawlerStateTest {

    // very important
    @Test
    public void testCombine() {
        assertThat(get(CrawlerState.ERROR, CrawlerState.BATCH_FINISHED)).isEqualTo(CrawlerState.BATCH_FINISHED);
        assertThat(get(CrawlerState.NOT_RUNNING, CrawlerState.RUNNING)).isEqualTo(CrawlerState.RUNNING);
        assertThat(get(CrawlerState.RUNNING, CrawlerState.ERROR)).isEqualTo(CrawlerState.RUNNING);

        // state transitions
        assertThat(get(CrawlerState.RUNNING_WITH_DEPTH_REACHED, CrawlerState.BATCH_FINISHED)).isEqualTo(CrawlerState.BATCH_FINISHED_WITH_DEPTH_REACHED);
        assertThat(get(CrawlerState.BATCH_FINISHED_WITH_DEPTH_REACHED, CrawlerState.NOT_RUNNING)).isEqualTo(CrawlerState.NOT_RUNNING_WITH_DEPTH_REACHED);
    }

    private CrawlerState get(CrawlerState left, CrawlerState right) {
        return left.combine(right);
    }
}
