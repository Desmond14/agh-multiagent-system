package pl.edu.agh.agents.configuration;

public class AgentConfiguration {
    private String name;
    private Double carWidth;
    private Double carLength;
    private Double initialPosition;
    private Integer initialVelocity;
    private Integer maxVelocity;
    private Integer acceleration;
    private Integer streetNumber;
    private Double timeToStopThreshold;
    private Double safetyDistance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCarWidth() {
        return carWidth;
    }

    public void setCarWidth(Double carWidth) {
        this.carWidth = carWidth;
    }

    public Double getCarLength() {
        return carLength;
    }

    public void setCarLength(Double carLength) {
        this.carLength = carLength;
    }

    public Double getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(Double initialPosition) {
        this.initialPosition = initialPosition;
    }

    public Integer getInitialVelocity() {
        return initialVelocity;
    }

    public void setInitialVelocity(Integer initialVelocity) {
        this.initialVelocity = initialVelocity;
    }

    public Integer getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(Integer maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public Integer getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Integer acceleration) {
        this.acceleration = acceleration;
    }

    public Integer getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(Integer streetNumber) {
        this.streetNumber = streetNumber;
    }

    public Double getTimeToStopThreshold() {
        return timeToStopThreshold;
    }

    public void setTimeToStopThreshold(Double timeToStopThreshold) {
        this.timeToStopThreshold = timeToStopThreshold;
    }

    public Double getSafetyDistance() {
        return safetyDistance;
    }

    public void setSafetyDistance(Double safetyDistance) {
        this.safetyDistance = safetyDistance;
    }
}

