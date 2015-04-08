package pl.edu.agh.agents.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import pl.edu.agh.agents.gui.Main;
import pl.edu.agh.agents.gui.Point;

/**
 * Created by S³awek on 2015-04-07.
 */
public class DriverBehaviour extends Behaviour {

    private Main gui;
    private int velocity_x;
    private int velocity_y;
    private int max_velocity_x;
    private int max_velocity_y;

    public DriverBehaviour(Agent agent, Object[] args) {
        super(agent);
        this.gui = (Main) args[0];
        this.velocity_x = (Integer) args[1];
        this.velocity_y = (Integer) args[2];
        this.max_velocity_x = (Integer) args[3];
        this.max_velocity_y = (Integer) args[4];
    }

    @Override
    public void action() {
        //tymczasowo samochod wykonuje ruch tylko gdy dostanie wiadomosc od Supervizora - domyslnie z danymi o innych aktorach
        ACLMessage msg = myAgent.receive();
        if(msg != null) {

            if(velocity_x < max_velocity_x) {
                velocity_x += 100;
            }
            if(velocity_y < max_velocity_y) {
                velocity_y += 100;
            }

            myAgent.addBehaviour(new MoveCarBehaviour(myAgent, gui, new Point(velocity_x, velocity_y)));

            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(myAgent.getLocalName() + ";velocity_x" + velocity_x + "velocity_y" + velocity_y);
        }
        block();
    }

    @Override
    public boolean done() {
        return false;
    }
}
