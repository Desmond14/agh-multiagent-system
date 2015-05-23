package pl.edu.agh.agents.gui;

import javafx.scene.paint.Color;

public class Car {
    private Color color;
    private Point upperLeft;
    private Double height;
    private Double width;

    public Car(Point upperLeft, Double height, Double width, Color color) {
        this.upperLeft = upperLeft;
        this.height = height;
        this.width = width;
        this.color = color;
    }

    public Point getUpperLeft() {
        return upperLeft;
    }

    public void setUpperLeft(Point upperLeft) {
        this.upperLeft = upperLeft;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
