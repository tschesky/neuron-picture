package pl.agh.edu.to.neuronpicture.webcrawler.utils;

import pl.agh.edu.to.neuronpicture.webcrawler.crawler.CrawlerState;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Sebastian on 2016-12-18.
 */

public class Utils {

    private Utils() {
    }

    public static <T> T setup(T a, Supplier<?> action) {
        action.get();
        return a;
    }

    public static <T> T setup(T a, Consumer<T> action) {
        action.accept(a);
        return a;
    }

    public static <T> Void toVoid(T t, Function<T, ?> function) {
        function.apply(t);
        return null;
    }

    public static <T> Void toVoid(Supplier<T> supplier) {
        supplier.get();
        return null;
    }

    public static <T> Void toVoid(Consumer<T> supplier) {
        supplier.accept(null);
        return null;
    }

    public static <T> CrawlerState toError(Consumer<T> supplier) {
        supplier.accept(null);
        return CrawlerState.ERROR;
    }

    public static URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            // checked exception sucks
        }
        return null;
    }

    public static Optional<URL> toURLOpt(String url) {
        return toURLOpt(url, null);
    }

    public static Optional<URL> toURLOpt(String url, Consumer<String> verbose) {
        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException e) {
            if (verbose != null) {
                verbose.accept(e.getMessage());
            }
        }
        return Optional.empty();
    }

    public static List<String> URLsToStrings(List<URL> urls) {
        return urls.stream().map(URL::toString).collect(Collectors.toList());
    }
}
