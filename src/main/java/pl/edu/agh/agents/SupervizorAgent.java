package pl.edu.agh.agents;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import javafx.scene.paint.Color;
import pl.edu.agh.agents.behaviours.WelcomeBehaviour;
import pl.edu.agh.agents.behaviours.WorldControlBehaviour;
import pl.edu.agh.agents.configuration.AgentConfiguration;
import pl.edu.agh.agents.configuration.ConfigurationLoader;
import pl.edu.agh.agents.gui.Car;
import pl.edu.agh.agents.gui.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupervizorAgent extends Agent {
    private static final String DRIVER_AGENT_CLASS = "pl.edu.agh.agents.DriverAgent";
    private Map<AID, Street> agentToStreet = new HashMap<>();
    public Map<Street, List<String>> agentsOnStreet = new HashMap<>();
    private PositionTranslator positionTranslator = new PositionTranslator(agentToStreet);
    public List<Driver> drivers;
    public Main gui;
    public Map<String, String> driversInfoMap = new HashMap<String, String>();
    private List<Street> streets;
    private List<Crossroad> crossroads;
    private AgentContainer agentContainer;

    public SupervizorAgent() {
        drivers = new ArrayList<Driver>();
        streets = new ArrayList<Street>();
    }

    public void setup() {

        setupStreets();
        agentsOnStreet.put(streets.get(0), new ArrayList<>());
        agentsOnStreet.put(streets.get(1), new ArrayList<>());

        agentContainer = getContainerController();
        Object[] arguments = getArguments();
        if (arguments.length == 0 || !(arguments[0] instanceof Main)) {
            throw new IllegalArgumentException();
        }
        gui = (Main) arguments[0];
        gui.assignStreetsToTrafficLanes(streets);
        crossroads = gui.calculateCrossroads();
        ConfigurationLoader configLoader = new ConfigurationLoader();
        try {
            configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            setupDrivers(configLoader);
            for(Driver driver : drivers) {
                Thread.sleep(1000);
                try {
                    driver.getController().start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
                addBehaviour(new WelcomeBehaviour(this, driver.getDriverAgentID(), driver.getCar(), gui));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        addBehaviour(new WorldControlBehaviour());
        setEnabledO2ACommunication(true, 1);
        addBehaviour(new SimpleBehaviour() {
            private boolean done = false;
            @Override
            public void action() {
                Object object = getO2AObject();
                if (object != null){
                    done = true;
                    Codec codec = new SLCodec();
                    Ontology jmo = JADEManagementOntology.getInstance();
                    getContentManager().registerLanguage(codec);
                    getContentManager().registerOntology(jmo);
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(getAMS());
                    msg.setLanguage(codec.getName());
                    msg.setOntology(jmo.getName());
                    System.out.println("About to kill platform");
                    try {
                        getContentManager().fillContent(msg, new Action(getAID(), new ShutdownPlatform()));
                        send(msg);
                    }
                    catch (Exception e) {}
                }
            }

            @Override
            public boolean done(){
                return done;
            }
        });
    }

    public void updateGui(String messageContent, AID sender) {
        CarMessage carMessage = MessageParser.parseCarMessage(messageContent);
        gui.moveCar(sender, positionTranslator.translatePosition(sender, carMessage.getCurrentPosition()));
    }

    private void setupStreets() {
        Street s1 = new Street(1, Direction.HORIZONTAL);
        Street s2 = new Street(2, Direction.VERTICAL);
        s1.addNeighbourStreet(s2);
        s2.addNeighbourStreet(s1);
        streets.add(s1);
        streets.add(s2);
        StreetMap.getInstance().setStreets(streets);
    }

    private void setupDrivers(ConfigurationLoader configLoader) throws StaleProxyException {

        List<AgentConfiguration> agentConfigurations = configLoader.getAgentConfigurations();
        for (AgentConfiguration config : agentConfigurations){
            AID agentID = new AID(config.getName(), AID.ISLOCALNAME);
            agentToStreet.put(agentID, getStreetByNumber(config.getStreetNumber()));
            agentsOnStreet.get(getStreetByNumber(config.getStreetNumber())).add(config.getName());
            Car car = new Car(positionTranslator.translatePosition(agentID, config.getInitialPosition()),
                    config.getCarLength(), config.getCarWidth(), Color.GREEN);
            //TODO: for now simple assumption that exactly one crossroad exists
            Double distanceToCrossroad = positionTranslator.translatePosition(agentID, crossroads.get(0).getUpperLeft());
            distanceToCrossroad -= config.getInitialPosition();
            Object[] args = new Object[] {config, gui, getStreetByNumber(config.getStreetNumber()), distanceToCrossroad};
            drivers.add(new Driver(agentID, agentContainer.createNewAgent(config.getName(),
                    DRIVER_AGENT_CLASS, args), car));
        }
    }

    public Street getStreetByNumber(int streetNumber) {
        for(Street street : streets) {
            if(street.getStreetNumber() == streetNumber) {
                return street;
            }
        }
        return null;
    }
}
