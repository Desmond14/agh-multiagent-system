package pl.edu.agh.agents;

public class CarMessage {

    private String driverName;
    private double currentPosition;
    private int streetNumber;
    private int velocity;
    private double distanceToCrossroad;
    private double carWidth;

    public CarMessage(String driverName, double currentPosition, int streetNumber, int velocity, double carWidth,
                      double distanceToCrossroad) {
        this.setDriverName(driverName);
        this.currentPosition = currentPosition;
        this.streetNumber = streetNumber;
        this.velocity = velocity;
        this.carWidth = carWidth;
        this.setDistanceToCrossroad(distanceToCrossroad);
    }

    public double getCarWidth() {
        return carWidth;
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

    public double getDistanceToCrossroad() {
        return distanceToCrossroad;
    }

    public void setDistanceToCrossroad(double distanceToCrossroad) {
        this.distanceToCrossroad = distanceToCrossroad;
    }

    @Override
    public String toString() {
        return getDriverName() + ";" + currentPosition + ";" +
                streetNumber + ";" + velocity + ";" +
                carWidth + ";" + getDistanceToCrossroad();
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
