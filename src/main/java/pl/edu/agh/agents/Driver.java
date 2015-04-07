package pl.edu.agh.agents;

import jade.core.AID;
import jade.wrapper.AgentController;
import pl.edu.agh.agents.gui.Car;
import pl.edu.agh.agents.gui.Point;

/**
 * Created by S³awek on 2015-04-06.
 */
public class Driver {

    private Car car;
    private AID driverAgentID;
    private AgentController controller;
    private Point pointAfterMove;

    public Driver(AID driverAgentID, AgentController controller, Car car, Point pointAfterMove) {
        this.driverAgentID = driverAgentID;
        this.controller = controller;
        this.car = car;
        this.pointAfterMove = pointAfterMove;
    }

    public AID getDriverAgentID()
    {
        return driverAgentID;
    }
    public AgentController getController() {
        return controller;
    }

    public Car getCar() {
        return car;
    }

    public Point getPointAfterMove(){
        return pointAfterMove;
    }

    public void setPointAfterMove(Point point) {
        this.pointAfterMove = point;
    }
}
