package pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters.image;

import org.jsoup.nodes.Element;
import org.junit.Test;
import pl.agh.edu.to.neuronpicture.webcrawler.persistance.DownloadedImage;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Sebastian on 2017-01-13.
 */

public class ImageFormatFilterTest {

    private final ImageFilter filter = new ImageFormatFilter("png", "gif");

    @Test
    public void testElementWithWrongExtension() {
        Element element = getMockElement("nazwa.jpg");

        assertThat(filter.test(element)).isFalse();
    }

    @Test
    public void testElementWithAllowedExtension() {
        Element element = getMockElement("nazwa.png");

        assertThat(filter.test(element)).isTrue();
    }

    @Test
    public void testImageAlwaysTrue() {
        DownloadedImage image = getMockDownloadedImage();

        assertThat(filter.test(image)).isTrue();
    }

    private DownloadedImage getMockDownloadedImage() {
        return mock(DownloadedImage.class);
    }

    private Element getMockElement(String src) {
        Element element = mock(Element.class);
        when(element.attr("src")).thenReturn(src);
        return element;
    }
}
