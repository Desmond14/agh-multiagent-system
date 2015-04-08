package pl.edu.agh.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.scene.paint.Color;
import pl.edu.agh.agents.behaviours.MoveCarBehaviour;
import pl.edu.agh.agents.behaviours.WelcomeBehaviour;
import pl.edu.agh.agents.gui.Car;
import pl.edu.agh.agents.gui.Main;
import pl.edu.agh.agents.gui.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupervizorAgent extends Agent {
    //private Map<AID, AgentController> driverAgents;
    private List<Driver> drivers;
    private Main gui;

    public SupervizorAgent() {

        //driverAgents = new HashMap<AID, AgentController>();
        drivers = new ArrayList<Driver>();
    }

    public void setup() {
        AgentContainer agentContainer = getContainerController();
        Object[] arguments = getArguments();
        if (arguments.length == 0 || !(arguments[0] instanceof Main)) {
            throw new IllegalArgumentException();
        }
        gui = (Main) arguments[0];

        try {
            String name = "Driver_Dave";
            AID agentID = new AID(name, AID.ISLOCALNAME);
            Car car = new Car(new Point(0, 280), 40, 40, Color.ORANGE);
            int velocity_x = 100;
            int velocity_y = 300;
            int max_velocity_x = 1000;
            int max_velocity_y = 300;
            Object[] args = new Object[5];
            args[0] = gui;
            args[1] = velocity_x;
            args[2] = velocity_y;
            args[3] = max_velocity_x;
            args[4] = max_velocity_y;
            drivers.add(new Driver(agentID, agentContainer.createNewAgent(name, "pl.edu.agh.agents.DriverAgent", args),
                    car, new Point(100, 300)));
//            Thread.sleep(1000);
//            gui.addCar(agentID, car);
//            Thread.sleep(1000);
//            gui.moveCar(agentID, new Point(780, 300));

            for(Driver driver : drivers) {
                Thread.sleep(1000);
                try {
                    driver.getController().start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
                addBehaviour(new WelcomeBehaviour(this, driver.getDriverAgentID(), driver.getCar(), gui));
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        addBehaviour(new SimpleBehaviour() {
            @Override
            public void action() {
                //otrzymywanie wiadomosci od agentow o ich predkosci, polozeniu itp.
                ACLMessage msg = receive();
            }

            @Override
            public boolean done() {
                return false;
            }
        });
    }
}
