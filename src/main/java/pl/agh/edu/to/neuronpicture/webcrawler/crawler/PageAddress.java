package pl.agh.edu.to.neuronpicture.webcrawler.crawler;

import pl.agh.edu.to.neuronpicture.webcrawler.utils.Utils;

import java.net.URL;
import java.util.Optional;

/**
 * Created by Sebastian on 2017-01-02.
 */

public class PageAddress {

    public static class Builder {

        private String url;
        private int depth = 0;

        public Builder() {
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder depth(int depth) {
            this.depth = depth;
            return this;
        }

        public Optional<PageAddress> build() {
            return Utils.toURLOpt(this.url).map(url -> new PageAddress(url, depth));
        }
    }

    private final URL url;
    private final int depth;

    public PageAddress(URL url) {
        this(url, 0);
    }

    public PageAddress(URL url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    public String getUrlString() {
        String string = url.toString();
        if (string.endsWith("/")) {
            return string.substring(0, string.length()-1);
        } else {
            return string;
        }
    }

    public URL getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }
}
