package pl.edu.agh.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Created by S³awek on 2015-03-27.
 */
public class DriverAgent extends Agent {
    public void setup() {
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if(msg != null) {
                    System.out.println(myAgent.getLocalName() + " received message: "
                            + msg.getContent());
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(getLocalName() + " is placed on the map");
                    send(reply);
                }
                block();
            }
        });
    }
}
