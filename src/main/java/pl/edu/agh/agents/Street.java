package pl.edu.agh.agents;

import java.util.ArrayList;
import java.util.List;

public class Street {

    private int streetNumber;
    private List<Street> neighbourStreets;
    private Direction direction;

    public Street(int streetNumber, Direction direction) {
        this.streetNumber = streetNumber;
        this.setDirection(direction);
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

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
