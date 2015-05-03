package pl.edu.agh.agents.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import pl.edu.agh.agents.CarMessage;
import pl.edu.agh.agents.MessageParser;
import pl.edu.agh.agents.gui.Car;
import pl.edu.agh.agents.gui.Main;
import pl.edu.agh.agents.gui.Point;

import java.util.List;

/**
 * Created by S�awek on 2015-04-07.
 */
public class DriverBehaviour extends Behaviour {

    private Main gui;
    private int velocity_x;
    private int velocity_y;
    private int max_velocity_x;
    private int max_velocity_y;
    private Car car;
    private Point currPosition;
    private int streetNumber;
    private boolean done;
    private int state = 0;

    public DriverBehaviour(Agent agent, Object[] args) {
        super(agent);
        this.gui = (Main) args[0];
        this.velocity_x = (Integer) args[1];
        this.velocity_y = (Integer) args[2];
        this.max_velocity_x = (Integer) args[3];
        this.max_velocity_y = (Integer) args[4];
        this.car = (Car) args[5];
        this.currPosition = car.getUpperLeft();
        this.streetNumber = (Integer) args[6];
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();

        if(msg != null) {
            switch (state) {
                case 0:
                    //jesli to dopiero wiadomosc inicjalizująca agenta

                    state += 1;
                    break;

                case 1:
                    List<CarMessage> driversInfo = MessageParser.getCarMessages(msg.getContent(), myAgent.getName());
                    for(CarMessage carInfo : driversInfo) {
                        if(carInfo.getStreetNumber() == streetNumber) {
                            //jeśli dwaj kierowcy znajdują się na jednej ulicy, to nie mogą w siebie wjeżdżać
                            Point carPosition = carInfo.getCarPosition();
                            int carVelocity_x = carInfo.getVelocity_x();
                            int carVelocity_y = carInfo.getVelocity_y();
                        }
                    }
            }
        }


        //zachowanie domyślne - przyspiesza +100 w wymiarze, w którym się znajduje (velocity_x, velocity_y)
        // aż do osiągnięcia maksymalnej predkosci
        if(msg != null) {
            String direction;
            if(velocity_x < max_velocity_x) {
                velocity_x += 1;
            }
            if(velocity_y < max_velocity_y) {
                velocity_y += 1;
            }

            Point positionAfterMove = new Point(currPosition.getX() + velocity_x, currPosition.getY() + velocity_y);
            System.out.println("Moving agent " + myAgent.getLocalName() + " from " + currPosition + " to " + positionAfterMove);
            myAgent.addBehaviour(new MoveCarBehaviour(myAgent, gui, positionAfterMove));

            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(new CarMessage(myAgent.getLocalName(), positionAfterMove, streetNumber, velocity_x, velocity_y).toString());
            myAgent.send(reply);
            currPosition = positionAfterMove;
            done = true;
        }
        block();
    }

    @Override
    public boolean done() {
        return false;
    }
}
