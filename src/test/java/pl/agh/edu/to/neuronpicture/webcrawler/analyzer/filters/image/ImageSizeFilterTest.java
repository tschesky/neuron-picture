package pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image;

import org.jsoup.nodes.Element;
import org.junit.Test;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.DownloadedImage;

import java.awt.image.BufferedImage;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Sebastian on 2017-01-13.
 */

public class ImageSizeFilterTest {

    private ImageFilter filter = new ImageSizeFilter(50, 50);

    @Test
    public void testBigEnoughImage() {
        DownloadedImage image = getMockDownloadedImage(100, 100);

        assertThat(filter.test(image)).isTrue();
    }

    @Test
    public void testTooSmallImage() {
        DownloadedImage image = getMockDownloadedImage(20, 120);

        assertThat(filter.test(image)).isFalse();
    }

    @Test
    public void testBigEnoughElement() {
        Element element = getMockElement("100", "100");

        assertThat(filter.test(element)).isTrue();
    }

    @Test
    public void testTooSmallElement() {
        Element element = getMockElement("10", "120");

        assertThat(filter.test(element)).isFalse();
    }

    @Test
    public void testMalformedElement() {
        Element element = getMockElement("bad", "120");

        assertThat(filter.test(element)).isTrue();
    }

    private DownloadedImage getMockDownloadedImage(int width, int height) {
        BufferedImage image = mock(BufferedImage.class);
        when(image.getHeight()).thenReturn(height);
        when(image.getWidth()).thenReturn(width);
        DownloadedImage i = mock(DownloadedImage.class);
        when(i.getImage()).thenReturn(image);
        return i;
    }

    private Element getMockElement(String width, String height) {
        Element element = mock(Element.class);
        when(element.attr("width")).thenReturn(width);
        when(element.attr("height")).thenReturn(height);
        return element;
    }
}
