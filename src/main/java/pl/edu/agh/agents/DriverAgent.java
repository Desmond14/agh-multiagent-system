package pl.edu.agh.agents;

import jade.core.Agent;
import pl.edu.agh.agents.behaviours.DriverBehaviour;
import pl.edu.agh.agents.configuration.AgentConfiguration;
import pl.edu.agh.agents.gui.Main;

/**
 * Created by S³awek on 2015-03-27.
 */
public class DriverAgent extends Agent {

    private AgentConfiguration configuration;
    private Main gui;
    private Street street;
    public AgentConfiguration getConfiguration() { return configuration; }
    public Main getGui() { return gui; }
    public Street getStreet() { return street; }
    public void setConfiguration(AgentConfiguration configuration) { this.configuration = configuration; }

    public void setup() {

        Object[] args = getArguments();
        configuration = (AgentConfiguration) args[0];
        gui = (Main) args[1];
        street = (Street) args[2];
        addBehaviour(new DriverBehaviour(this, (Double)args[3]));
    }
}
