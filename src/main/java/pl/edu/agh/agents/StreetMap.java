package pl.edu.agh.agents;

import java.util.List;

/**
 * Created by S³awek on 2015-05-01.
 */
public class StreetMap {
    private List<Street> streets;
    private static StreetMap instance = null;

    private StreetMap() { }

    public static StreetMap getInstance() {
        if(instance == null) {
            instance = new StreetMap();
        }
        return instance;
    }

    public List<Street> getStreets() { return streets; }
    public void setStreets(List<Street> streets) { this.streets = streets; }
}
