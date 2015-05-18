package pl.edu.agh.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import javafx.scene.paint.Color;
import pl.edu.agh.agents.behaviours.WelcomeBehaviour;
import pl.edu.agh.agents.gui.Car;
import pl.edu.agh.agents.gui.Main;
import pl.edu.agh.agents.gui.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupervizorAgent extends Agent {
    private Map<AID, Street> agentToStreet = new HashMap<>();
    private Map<Street, List<String>> agentsOnStreet = new HashMap<>();
    private PositionTranslator positionTranslator = new PositionTranslator(agentToStreet);
    private List<Driver> drivers;
    private Main gui;
    private Map<String, String> driversInfoMap = new HashMap<String, String>();
    private int messageCount = 0;

    public SupervizorAgent() {
        drivers = new ArrayList<Driver>();
    }

    public void setup() {
        List<Street> streets = new ArrayList<Street>();
        Street s1 = new Street(1, Direction.HORIZONTAL);
        Street s2 = new Street(2, Direction.VERTICAL);
        s1.addNeighbourStreet(s2);
        s2.addNeighbourStreet(s1);
        streets.add(s1);
        streets.add(s2);
        StreetMap.getInstance().setStreets(streets);
        agentsOnStreet.put(s1, new ArrayList<>());
        agentsOnStreet.put(s2, new ArrayList<>());

        AgentContainer agentContainer = getContainerController();
        Object[] arguments = getArguments();
        if (arguments.length == 0 || !(arguments[0] instanceof Main)) {
            throw new IllegalArgumentException();
        }
        gui = (Main) arguments[0];

        try {
            //TODO: drivers configuration should be loaded from external resource, e.g. xml
            String name = "Dave";
            AID agentID = new AID(name, AID.ISLOCALNAME);
            Car car = new Car(new Point(0, 280), 40, 40, Color.GREEN);
            agentToStreet.put(agentID, s1);
            agentsOnStreet.get(s1).add(name);
            int velocity1 = 0;
            int maxVelocity1 = 20;
            int acceleration = 1;
            Object[] args = new Object[9];
            args[0] = car.getWidth();
            args[1] = velocity1;
            args[3] = maxVelocity1;
            args[5] = car.getUpperLeft().getX();
            args[6] = streets.get(0).getStreetNumber();
            args[8] = acceleration;
            drivers.add(new Driver(agentID, agentContainer.createNewAgent(name, "pl.edu.agh.agents.DriverAgent", args),
                    car, new Point(100, 300)));

//            String name2 = "Kate";
//            AID agentID2 = new AID(name2, AID.ISLOCALNAME);
//            Car car2 = new Car(new Point(380, 0), 40, 40, Color.ORANGE);
//            int velocity_x2 = 0;
//            int velocity_y2 = 2;
//            int max_velocity_x2 = 0;
//            int max_velocity_y2 = 17;
//            int acceleration2 = 3;
//            Object[] args2 = new Object[9];
//            args2[0] = gui;
//            args2[1] = velocity_x2;
//            args2[2] = velocity_y2;
//            args2[3] = max_velocity_x2;
//            args2[4] = max_velocity_y2;
//            args2[5] = car2;
//            args2[6] = streets.get(1).getStreetNumber();
//            args2[7] = Direction.Y_DOWN;
//            args2[8] = acceleration2;
//            drivers.add(new Driver(agentID2, agentContainer.createNewAgent(name2, "pl.edu.agh.agents.DriverAgent", args2),
//                    car2, new Point(100, 300)));


            String name3 = "John";
            AID agentID3 = new AID(name3, AID.ISLOCALNAME);
            Car car3 = new Car(new Point(235, 280), 40, 40, Color.BLUE);
            agentToStreet.put(agentID3, s1);
            agentsOnStreet.get(s1).add(name3);
            int velocity3 = 0;
            int maxVelocity3 = 1;
            int acceleration3 = 1;
            Object[] args3 = new Object[9];
            args3[0] = car3.getWidth();
            args3[1] = velocity3;
            args3[3] = maxVelocity3;
            args3[5] = car3.getUpperLeft().getX();
            args3[6] = streets.get(0).getStreetNumber();
            args3[8] = acceleration3;
            drivers.add(new Driver(agentID3, agentContainer.createNewAgent(name3, "pl.edu.agh.agents.DriverAgent", args3),
                    car3, new Point(100, 300)));

//            String name4 = "Ann";
//            AID agentID4 = new AID(name4, AID.ISLOCALNAME);
//            Car car4 = new Car(new Point(380, 50), 40, 40, Color.RED);
//            int velocity_x4 = 0;
//            int velocity_y4 = 1;
//            int max_velocity_x4 = 0;
//            int max_velocity_y4 = 10;
//            int acceleration4 = 2;
//            Object[] args4 = new Object[9];
//            args4[0] = gui;
//            args4[1] = velocity_x4;
//            args4[2] = velocity_y4;
//            args4[3] = max_velocity_x4;
//            args4[4] = max_velocity_y4;
//            args4[5] = car4;
//            args4[6] = streets.get(1).getStreetNumber();
//            args4[7] = Direction.Y_DOWN;
//            args4[8] = acceleration4;
//            drivers.add(new Driver(agentID4, agentContainer.createNewAgent(name4, "pl.edu.agh.agents.DriverAgent", args4),
//                    car4, new Point(100, 300)));

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
            //TODO: refactor this should be configurable
            public  final Double CARS_WIDTH = 40.0;
            //TODO: this method is much too complicated, should be splitted into multiple descriptive methods
            private boolean done = false;

            @Override
            public void action() {
                //otrzymywanie wiadomosci od agentow o ich predkosci, polozeniu itp.
                ACLMessage msg = receive();

                if(msg != null) {
                    messageCount++;
                    String sender = msg.getSender().getName().split("@")[0];
                    driversInfoMap.put(sender, msg.getContent());
                    updateGui(msg.getContent(), msg.getSender());
                    if(messageCount == drivers.size()) {
                        messageCount = 0;
                        StringBuilder stringBuilder = new StringBuilder();
                        for(Object value : driversInfoMap.values()) {
                            stringBuilder.append(value);
                            stringBuilder.append("/");
                        }

                        String driversInfoMsg = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);

                        if (collisionDetected()){
                            gui.displayText("Collision detected!");
                            done = true;
                            return;
                        }

                        for (Driver driver : drivers) {
                            ACLMessage broadcastMsg = new ACLMessage(ACLMessage.INFORM);
                            //broadcastMsg.setContent(msg.getContent());
                            broadcastMsg.setContent(driversInfoMsg);
                            broadcastMsg.addReceiver(driver.getDriverAgentID());
                            send(broadcastMsg);
                        }

                        try {
                            Thread.sleep(200);
                            System.out.println("Waiting for next round");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            //TODO: refactor ASAP as well as whole class SupervizorAgent!
            private boolean collisionDetected() {
                for (Street street : agentsOnStreet.keySet()){
                    List<String> agents = agentsOnStreet.get(street);
                    for (String agent : agents) {
                        for (String otherAgent : agents) {
                            if (!agent.equals(otherAgent)) {
                                CarMessage message1 = MessageParser.parseCarMessage(driversInfoMap.get(agent));
                                CarMessage message2 = MessageParser.parseCarMessage(driversInfoMap.get(otherAgent));
                                if (collide(message1, message2)){
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }

            private boolean collide(CarMessage message1, CarMessage message2) {
                if (message1.getCurrentPosition() < message2.getCurrentPosition()) {
                    return message1.getCurrentPosition() + CARS_WIDTH >= message2.getCurrentPosition();
                } else {
                    return message2.getCurrentPosition() + CARS_WIDTH >= message1.getCurrentPosition();
                }
            }

            @Override
            public boolean done() {
                return done;
            }
        });
    }

    private void updateGui(String messageContent, AID sender) {
        CarMessage carMessage = MessageParser.parseCarMessage(messageContent);
        gui.moveCar(sender, positionTranslator.translatePosition(sender, carMessage.getCurrentPosition()));
    }
}
