package pl.edu.agh.agents.gui;

import pl.edu.agh.agents.Street;

public class TrafficLane {
    private final Point upperLeft;
    private final Point bottomRight;
    private Street street;

    public TrafficLane(Point upperLeft, Point bottomRight) {
        this.upperLeft = upperLeft;
        this.bottomRight = bottomRight;
    }

    public Point getUpperLeft() {
        return upperLeft;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {

        this.street = street;
    }

    public Point getBottomRight() {
        return bottomRight;
    }
}
