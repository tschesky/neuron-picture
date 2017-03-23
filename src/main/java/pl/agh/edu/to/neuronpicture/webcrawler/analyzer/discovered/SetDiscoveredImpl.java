package pl.agh.edu.to.neuronpicture.webcrawler.analyzer.discovered;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Sebastian on 2016-12-30.
 */

public class SetDiscoveredImpl implements Discovered {

    private final Set<String> discovered = new ConcurrentSkipListSet<>();


    public SetDiscoveredImpl() {
    }

    public SetDiscoveredImpl(List<String> seed) {
        discovered.addAll(seed);
    }

    @Override
    public boolean checkAndSave(String element) {
        return discovered.add(element);
    }

    @Override
    public boolean check(String element) {
        return discovered.contains(element);
    }
}
