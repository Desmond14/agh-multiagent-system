package pl.edu.agh.agents.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import pl.edu.agh.agents.gui.Car;
import pl.edu.agh.agents.gui.Main;
import pl.edu.agh.agents.gui.Point;

/**
 * Created by S�awek on 2015-03-27.
 */
public class WelcomeBehaviour extends Behaviour {

    private AID driverAgentID;
    private Car car;
    private Main gui;
    private boolean addedCar = false;

    public WelcomeBehaviour(Agent agent, AID driverAgentID, Car car, Main gui) {
        super(agent);
        this.driverAgentID = driverAgentID;
        this.car = car;
        this.gui = gui;
    }

    @Override
    public void action() {
//        System.out.println("I will send message to: " + driverAgentID);
//        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//        msg.setContent("Welcome agent " + driverAgentID);
//        msg.addReceiver(driverAgentID);
//        myAgent.send(msg);
        gui.addCar(driverAgentID, car);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        addedCar = true;
        block();

        myAgent.addBehaviour(new SimpleBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent("Welcome agent " + driverAgentID);
                msg.addReceiver(driverAgentID);
                myAgent.send(msg);
                block(2000);
            }

            @Override
            public boolean done() {
                return addedCar;
            }
        });
    }

    @Override
    public boolean done() {
        return addedCar;
    }
}
