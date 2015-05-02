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
    private Map<String, String> driversInfoMap = new HashMap<String, String>();
    private int messageCount = 0;

    public SupervizorAgent() {

        //driverAgents = new HashMap<AID, AgentController>();
        drivers = new ArrayList<Driver>();
    }

    public void setup() {
        List<Street> streets = new ArrayList<Street>();
        Street s1 = new Street(1);
        Street s2 = new Street(2);
        s1.addNeighbourStreet(s2);
        s2.addNeighbourStreet(s1);
        streets.add(s1);
        streets.add(s2);
        StreetMap.getInstance().setStreets(streets);

        AgentContainer agentContainer = getContainerController();
        Object[] arguments = getArguments();
//        if (arguments.length == 0 || !(arguments[0] instanceof Main)) {
//            throw new IllegalArgumentException();
//        }
//        gui = (Main) arguments[0];

        try {
            String name = "Dave";
            AID agentID = new AID(name, AID.ISLOCALNAME);
            Car car = new Car(new Point(0, 280), 40, 40, Color.ORANGE);
            int velocity_x = 100;
            int velocity_y = 0;
            int max_velocity_x = 1000;
            int max_velocity_y = 0;
            Object[] args = new Object[7];
            args[0] = gui;
            args[1] = velocity_x;
            args[2] = velocity_y;
            args[3] = max_velocity_x;
            args[4] = max_velocity_y;
            args[5] = car;
            args[6] = streets.get(0).getStreetNumber();
            drivers.add(new Driver(agentID, agentContainer.createNewAgent(name, "pl.edu.agh.agents.DriverAgent", args),
                    car, new Point(100, 300)));

            String name2 = "Kate";
            AID agentID2 = new AID(name2, AID.ISLOCALNAME);
            Car car2 = new Car(new Point(0, 280), 40, 40, Color.ORANGE);
            int velocity_x2 = 100;
            int velocity_y2 = 0;
            int max_velocity_x2 = 100;
            int max_velocity_y2 = 0;
            Object[] args2 = new Object[7];
            args2[0] = gui;
            args2[1] = velocity_x2;
            args2[2] = velocity_y2;
            args2[3] = max_velocity_x2;
            args2[4] = max_velocity_y2;
            args2[5] = car2;
            args2[6] = streets.get(1).getStreetNumber();
            drivers.add(new Driver(agentID2, agentContainer.createNewAgent(name2, "pl.edu.agh.agents.DriverAgent", args2),
                    car2, new Point(100, 300)));

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


                if(msg != null) {
                    messageCount++;
                    driversInfoMap.put(msg.getSender().getName(), msg.getContent());
                    if(messageCount == drivers.size()) {
                        messageCount = 0;
                        StringBuilder stringBuilder = new StringBuilder();
                        for(Object value : driversInfoMap.values()) {
                            stringBuilder.append(value);
                            stringBuilder.append("/");
                        }

                        String driversInfoMsg = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);

                        for (Driver driver : drivers) {
                            ACLMessage broadcastMsg = new ACLMessage(ACLMessage.INFORM);
                            //broadcastMsg.setContent(msg.getContent());
                            broadcastMsg.setContent(driversInfoMsg);
                            broadcastMsg.addReceiver(driver.getDriverAgentID());
                            send(broadcastMsg);
                        }
                    }

                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });
    }
}
