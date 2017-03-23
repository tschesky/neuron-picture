package pl.agh.edu.to.neuronpicture.webcrawler.analyzer.discovered;

/**
 * Created by Sebastian on 2016-12-30.
 */

public interface Discovered {
    boolean checkAndSave(String element);
    boolean check(String element);
}
