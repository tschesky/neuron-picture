package pl.agh.edu.to.neuronpicture.webcrawler.utils;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.filter.ThrottleRequestFilter;

/**
 * Created by Sebastian on 2016-12-19.
 */

public class HttpClientFactory {

    private HttpClientFactory() {
    }

    public static AsyncHttpClient getDefaultClient() {
        return getThrottledClient(8);
    }

    public static AsyncHttpClient getThrottledClient(int connections) {
        AsyncHttpClientConfig asyncHttpClientConfig = new DefaultAsyncHttpClientConfig.Builder()
                .addRequestFilter(new ThrottleRequestFilter(connections))
                .setMaxConnectionsPerHost(4)
                .build();
        return new DefaultAsyncHttpClient(asyncHttpClientConfig);
    }
}
