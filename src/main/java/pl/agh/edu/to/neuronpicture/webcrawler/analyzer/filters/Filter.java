package pl.agh.edu.to.neuronpicture.webcrawler.analyzer.filters;

import org.jsoup.nodes.Element;

import java.util.function.Predicate;

/**
 * Created by Sebastian on 2016-12-30.
 */

public interface Filter extends Predicate<Element> {

}
