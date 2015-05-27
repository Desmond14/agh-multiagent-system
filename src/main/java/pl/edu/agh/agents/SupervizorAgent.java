package pl.edu.agh.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import javafx.scene.paint.Color;
import pl.edu.agh.agents.behaviours.WelcomeBehaviour;
import pl.edu.agh.agents.configuration.AgentConfiguration;
import pl.edu.agh.agents.configuration.ConfigurationLoader;
import pl.edu.agh.agents.gui.Car;
import pl.edu.agh.agents.gui.Main;
import pl.edu.agh.agents.gui.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupervizorAgent extends Agent {
    private static final String DRIVER_AGENT_CLASS = "pl.edu.agh.agents.DriverAgent";
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
        ConfigurationLoader configLoader = new ConfigurationLoader();
        try {
            configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        List<AgentConfiguration> agentConfigurations = configLoader.getAgentConfigurations();

        try {
            for (AgentConfiguration config : agentConfigurations){
                AID agentID = new AID(config.getName(), AID.ISLOCALNAME);
                agentToStreet.put(agentID, s1);
                agentsOnStreet.get(s1).add(config.getName());
                Car car = new Car(positionTranslator.translatePosition(agentID, config.getInitialPosition()),
                        config.getCarLength(), config.getCarWidth(), Color.GREEN);
                Object[] args = new Object[] {config};
                drivers.add(new Driver(agentID, agentContainer.createNewAgent(config.getName(),
                        DRIVER_AGENT_CLASS, args), car));
            }

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
                    return message1.getCurrentPosition() + message1.getCarWidth() >= message2.getCurrentPosition();
                } else {
                    return message2.getCurrentPosition() + message1.getCarWidth() >= message1.getCurrentPosition();
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
