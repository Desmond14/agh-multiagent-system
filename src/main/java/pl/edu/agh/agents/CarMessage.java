package pl.edu.agh.agents;

import pl.edu.agh.agents.gui.Point;

/**
 * Created by S�awek on 2015-04-25.
 */
public class CarMessage {

    private String driverName;
    private Point carPosition;
    private int streetNumber;
    private int velocity_x;
    private int velocity_y;

    public CarMessage(String driverName, Point carPosition, int streetNumber, int velocity_x, int velocity_y) {
        this.driverName = driverName;
        this.carPosition = carPosition;
        this.streetNumber = streetNumber;
        this.velocity_x = velocity_x;
        this.velocity_y = velocity_y;
    }

    public String getDriverName() {
        return driverName;
    }

    public Point getCarPosition() {
        return carPosition;
    }

    public int getVelocity_x() {
        return velocity_x;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public int getVelocity_y() {
        return velocity_y;
    }

    @Override
    public String toString() {
        return driverName + ";" + carPosition.getX() + ";" + carPosition.getY() + ";" + streetNumber + ";" + velocity_x + ";" + velocity_y;
    }
}
