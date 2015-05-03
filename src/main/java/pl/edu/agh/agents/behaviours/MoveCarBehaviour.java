package pl.edu.agh.agents.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import pl.edu.agh.agents.Driver;
import pl.edu.agh.agents.gui.Car;
import pl.edu.agh.agents.gui.Main;
import pl.edu.agh.agents.gui.Point;

/**
 * Created by S�awek on 2015-04-06.
 */
public class MoveCarBehaviour extends Behaviour {

    private Main gui;
    private Point moveToPoint;
    private boolean doneThat = false;

    public MoveCarBehaviour(Agent agent, Main gui, Point moveToPoint) {
        super(agent);
        this.gui = gui;
        this.moveToPoint = moveToPoint;
    }


    @Override
    public void action() {
        gui.moveCar(myAgent.getAID(), moveToPoint);
        doneThat = true;
        myAgent.addBehaviour(new SimpleBehaviour() {
            @Override
            public void action() {
                //tutaj moze bedzie odpowiedz do supervizora o naszym stanie
            }

            @Override
            public boolean done() {
                return false;
            }
        });
    }

    @Override
    public boolean done() {
        return doneThat;
    }
}
