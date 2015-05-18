package pl.edu.agh.agents;

public class CarMessage {

    private String driverName;
    private double currentPosition;
    private int streetNumber;
    private int velocity;

    public CarMessage(String driverName, double currentPosition, int streetNumber, int velocity) {
        this.driverName = driverName;
        this.currentPosition = currentPosition;
        this.streetNumber = streetNumber;
        this.velocity = velocity;
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
                streetNumber + ";" + velocity;
    }
}
