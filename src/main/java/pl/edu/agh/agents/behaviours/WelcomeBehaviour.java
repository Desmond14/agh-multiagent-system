package pl.edu.agh.agents.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by S³awek on 2015-03-27.
 */
public class WelcomeBehaviour extends Behaviour {

    private AID driverAgentID;

    public WelcomeBehaviour(Agent agent, AID driverAgentID) {
        super(agent);
        this.driverAgentID = driverAgentID;
    }

    @Override
    public void action() {
        System.out.println("I will send message to: " + driverAgentID);
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("Welcome agent " + driverAgentID);
        msg.addReceiver(driverAgentID);
        myAgent.send(msg);
        block(1000);
    }

    @Override
    public boolean done() {
        return false;
    }
}
