package pl.edu.agh.agents;

import jade.core.Agent;
import pl.edu.agh.agents.behaviours.DriverBehaviour;
import pl.edu.agh.agents.configuration.AgentConfiguration;

/**
 * Created by S³awek on 2015-03-27.
 */
public class DriverAgent extends Agent {

    private AgentConfiguration configuration;
    public AgentConfiguration getConfiguration() { return configuration; }
    public void setConfiguration(AgentConfiguration configuration) { this.configuration = configuration; }

    public void setup() {

        Object[] args = getArguments();
        configuration = (AgentConfiguration) args[0];

        addBehaviour(new DriverBehaviour(this));
    }
}
