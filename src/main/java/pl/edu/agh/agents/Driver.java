package pl.edu.agh.agents;

import jade.core.AID;
import jade.wrapper.AgentController;
import pl.edu.agh.agents.gui.Car;
import pl.edu.agh.agents.gui.Point;

public class Driver {

    private Car car;
    private AID driverAgentID;
    private AgentController controller;

    public Driver(AID driverAgentID, AgentController controller, Car car) {
        this.driverAgentID = driverAgentID;
        this.controller = controller;
        this.car = car;
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

}
