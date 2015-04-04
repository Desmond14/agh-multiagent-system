package pl.edu.agh.agents.gui;

import javafx.scene.paint.Color;

public class Car {
    private Color color = Color.ORANGE;
    private Point upperLeft;
    private int height;
    private int width;

    public Car(Point upperLeft, int height, int width) {
        this.upperLeft = upperLeft;
        this.height = height;
        this.width = width;
    }

    public Point getUpperLeft() {
        return upperLeft;
    }

    public void setUpperLeft(Point upperLeft) {
        this.upperLeft = upperLeft;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
