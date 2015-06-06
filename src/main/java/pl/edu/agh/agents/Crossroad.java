package pl.edu.agh.agents;

import pl.edu.agh.agents.gui.Point;

/**
 * Created by Mere on 2015-06-05.
 */
public class Crossroad {
    private Point upperLeft;
    private Point bottomRight;
    private Street streetOne;
    private Street streetTwo;
    private boolean isCarOnCrossroad;

    public Point getUpperLeft() {
        return upperLeft;
    }

    public void setUpperLeft(Point upperLeft) {
        this.upperLeft = upperLeft;
    }

    public boolean isCarOnCrossroad() {
        return isCarOnCrossroad;
    }

    public void setIsCarOnCrossroad(boolean isCarOnCrossroad) {
        this.isCarOnCrossroad = isCarOnCrossroad;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(Point bottomRight) {
        this.bottomRight = bottomRight;
    }

    public Street getStreetOne() {
        return streetOne;
    }

    public void setStreetOne(Street streetOne) {
        this.streetOne = streetOne;
    }

    public Street getStreetTwo() {
        return streetTwo;
    }

    public void setStreetTwo(Street streetTwo) {
        this.streetTwo = streetTwo;
    }

    public Crossroad(Point upperLeft, Point bottomRight, Street streetOne, Street streetTwo) {
        this.upperLeft = upperLeft;
        this.bottomRight = bottomRight;
        this.streetOne = streetOne;
        this.streetTwo = streetTwo;
        isCarOnCrossroad = false;

    }
}
