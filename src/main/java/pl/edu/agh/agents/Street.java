package pl.edu.agh.agents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S³awek on 2015-05-01.
 */
public class Street {

    private int streetNumber;
    private List<Street> neighbourStreets;

    public Street(int streetNumber) {
        this.streetNumber = streetNumber;
        neighbourStreets = new ArrayList<Street>();
    }

    public int getStreetNumber() { return streetNumber; }
    public List<Street> getNeighboorStreets() { return neighbourStreets; }
    public void setNeighboorStreets(List<Street> neighbourStreets) {
        this.neighbourStreets = neighbourStreets;
    }

    public void addNeighbourStreet(Street street) {
        neighbourStreets.add(street);
    }
}
