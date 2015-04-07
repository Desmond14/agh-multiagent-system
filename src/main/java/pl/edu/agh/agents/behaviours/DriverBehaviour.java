package pl.edu.agh.agents.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import pl.edu.agh.agents.Driver;
import jade.lang.acl.ACLMessage;
import pl.edu.agh.agents.gui.Main;
import pl.edu.agh.agents.gui.Point;

/**
 * Created by S³awek on 2015-04-07.
 */
public class DriverBehaviour extends Behaviour {

    private Object[] args;
    private int x = 0;

    public DriverBehaviour(Agent agent, Object[] args) {
        super(agent);
        this.args = args;
    }

    @Override
    public void action() {
        //tymczasowo samochod wykonuje ruch tylko gdy dostanie wiadomosc od Supervizora - domyslnie z danymi o innych aktorach
        ACLMessage msg = myAgent.receive();
        if(msg != null) {
            System.out.println(myAgent.getLocalName() + " received message: " + msg.getContent());

            if(args != null) {
                Main gui = (Main) args[0];
                x += 100;
                myAgent.addBehaviour(new MoveCarBehaviour(myAgent, gui, new Point(x, 300)));
            }
        }
        block();
    }

    @Override
    public boolean done() {
        return false;
    }
}
