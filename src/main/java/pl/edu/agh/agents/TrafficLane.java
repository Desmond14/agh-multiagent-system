package pl.edu.agh.agents;

public class TrafficLane {
    private final Point upperLeft;
    private final Point bottomRight;

    public TrafficLane(Point upperLeft, Point bottomRight) {
        this.upperLeft = upperLeft;
        this.bottomRight = bottomRight;
    }

    public Point getUpperLeft() {
        return upperLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }
}
