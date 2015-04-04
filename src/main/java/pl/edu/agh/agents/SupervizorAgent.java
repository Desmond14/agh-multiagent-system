package pl.edu.agh.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import pl.edu.agh.agents.behaviours.WelcomeBehaviour;
import sun.applet.Main;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by S�awek on 2015-03-27.
 */
public class SupervizorAgent extends Agent {
    private Map<AID, AgentController> driverAgents;

    public SupervizorAgent() {
        driverAgents = new HashMap<AID, AgentController>();
    }

    public void setup() {
        Main.main(new String[0]);
//        AgentContainer agentContainer = getContainerController();
//        try {
//            String name = "Driver_Dave";
//            AID agentID = new AID(name, AID.ISLOCALNAME);
//            driverAgents.put(agentID, agentContainer.createNewAgent(name, "pl.edu.agh.agents.DriverAgent", null));
//        } catch (StaleProxyException e) {
//            e.printStackTrace();
//        }
//
//        for(Map.Entry<AID, AgentController> entry : driverAgents.entrySet()) {
//            try {
//                entry.getValue().start();
//            } catch (StaleProxyException e) {
//                e.printStackTrace();
//            }
//            addBehaviour(new WelcomeBehaviour(this, entry.getKey()));
//        }

    }
}
