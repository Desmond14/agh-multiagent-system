package pl.edu.agh.agents;

import jade.core.Agent;
import pl.edu.agh.agents.behaviours.DriverBehaviour;
/**
 * Created by S³awek on 2015-03-27.
 */
public class DriverAgent extends Agent {

    public void setup() {

        Object[] args = getArguments();

        addBehaviour(new DriverBehaviour(this, args));
    }
}
