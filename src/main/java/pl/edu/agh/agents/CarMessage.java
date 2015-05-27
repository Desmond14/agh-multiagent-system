package pl.edu.agh.agents;

import pl.edu.agh.agents.configuration.AgentConfiguration;

public class CarMessage {

    private String driverName;
    private double currentPosition;
    private int streetNumber;
    private int velocity;

    public double getCarWidth() {
        return carWidth;
    }

    private double carWidth;

    public CarMessage(String driverName, double currentPosition, int streetNumber, int velocity, double carWidth) {
        this.driverName = driverName;
        this.currentPosition = currentPosition;
        this.streetNumber = streetNumber;
        this.velocity = velocity;
        this.carWidth = carWidth;
    }

    public String getDriverName() {
        return driverName;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public Double getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Double currentPosition) {
        this.currentPosition = currentPosition;
    }

    @Override
    public String toString() {
        return driverName + ";" + currentPosition + ";" +
                streetNumber + ";" + velocity + ";" + carWidth;
    }
}
