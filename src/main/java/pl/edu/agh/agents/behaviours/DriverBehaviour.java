package pl.edu.agh.agents.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import pl.edu.agh.agents.CarMessage;
import pl.edu.agh.agents.Direction;
import pl.edu.agh.agents.MessageParser;
import pl.edu.agh.agents.gui.Car;
import pl.edu.agh.agents.gui.Main;
import pl.edu.agh.agents.gui.Point;

import javax.sql.PooledConnection;
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
    private Direction direction;
    private int acceleration;
    private boolean chanceForCollisionSameStreet;

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
        this.direction = (Direction.valueOf(args[7].toString()));
        this.acceleration = (Integer) args[8];
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        Point positionAfterMove = null;
        if (msg != null) {
            if (!msg.getContent().startsWith("Welcome")) {
                chanceForCollisionSameStreet = false;
                List<CarMessage> driversInfo = MessageParser.getCarMessages(msg.getContent(), myAgent.getName());
                for (CarMessage otherCarInfo : driversInfo) {
                    if (!otherCarInfo.getDriverName().equals(myAgent.getLocalName()) && otherCarInfo.getStreetNumber() == streetNumber
                            && chechIfCarIsBehind(currPosition, otherCarInfo.getCarPosition())) {
                        //jeśli dwaj kierowcy znajdują się na jednej ulicy, to nie mogą w siebie wjeżdżać

                        Point myPredictedUpperRightPosition = null;
                        if (velocity_x < max_velocity_x && direction.equals(Direction.X_RIGHT)) {
                            myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x + acceleration + car.getWidth(),
                                    currPosition.getY() + velocity_y);
                        }
                        //możliwe, że będzie trzeba odejmować car.getWidth()
                        else if (velocity_y < max_velocity_y && direction.equals(Direction.Y_DOWN)) {
                            myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x,
                                    currPosition.getY() + velocity_y + acceleration + car.getWidth());
                        } else if (velocity_x >= max_velocity_x && direction.equals(Direction.X_RIGHT)) {
                            myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x + car.getWidth(),
                                    currPosition.getY() + velocity_y);
                        }
                        //możliwe, że będzie trzeba odejmować car.getWidth()
                        else if (velocity_y == max_velocity_y && direction.equals(Direction.Y_DOWN)) {
                            myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x,
                                    currPosition.getY() + velocity_y + car.getWidth());
                        }

                        positionAfterMove = avoidCollision(myPredictedUpperRightPosition, otherCarInfo);
                    }
                }
            }



//            if (chanceForCollisionSameStreet) {
//                performMove(positionAfterMove, msg);
//            } else {
//                normalDrive(msg);
//            }
            if(positionAfterMove == null) {
                if (velocity_x < max_velocity_x) {
                    if(max_velocity_x - velocity_x >= acceleration) {
                        velocity_x += acceleration;
                    }
                    else {
                        velocity_x = max_velocity_x;
                    }

                }
                if (velocity_y < max_velocity_y) {
                    if(max_velocity_y - velocity_y >= acceleration) {
                        velocity_y += acceleration;
                    }
                    else {
                        velocity_y = max_velocity_y;
                    }
                }
                positionAfterMove = new Point(currPosition.getX() + velocity_x, currPosition.getY() + velocity_y);
            }
            performMove(positionAfterMove, msg);

        }



    }

    @Override
    public boolean done() {
        return false;
    }

    //dobrze, by nie zwracało samego pointa, ale też np. szybkość, jaką uzyskaliśmy (chyba, że da się to wyliczyć)
    public Point avoidCollision(Point myUpperRight, CarMessage otherCarInfo) {
        int tested_velocity = 0;
        if(direction.equals(Direction.X_RIGHT)) {
            tested_velocity = velocity_x + acceleration;
        }
        else if(direction.equals(Direction.Y_DOWN)) {
            tested_velocity = velocity_y + acceleration;
        }
        Point myTestedUpperRightPosition = myUpperRight;
        Point myFinalUpperLeftPosition = null;
        while(collisionHappens(myTestedUpperRightPosition, otherCarInfo.getCarPosition()) && tested_velocity >= 0) {
            //poczatkowo bede zmniejszac predkosc o 1

            if(direction.equals(Direction.X_RIGHT)) {
                myTestedUpperRightPosition = new Point(currPosition.getX() + tested_velocity + car.getWidth(),
                        currPosition.getY() + velocity_y);
                //myFinalUpperLeftPosition = new Point(currPosition.getX() + tested_velocity, currPosition.getY() + velocity_y);
            }
            else if(direction.equals(Direction.Y_DOWN)) {
                myTestedUpperRightPosition = new Point(currPosition.getX() + velocity_x,
                        currPosition.getY() + tested_velocity + car.getWidth());
                //myFinalUpperLeftPosition = new Point(currPosition.getX() + velocity_x, currPosition.getY() + tested_velocity);
            }
            tested_velocity--;
        }
        if(direction.equals(Direction.X_RIGHT)) {
            velocity_x = tested_velocity;
            myFinalUpperLeftPosition = new Point(currPosition.getX() + velocity_x, currPosition.getY() + velocity_y);
        }
        else if(direction.equals(Direction.Y_DOWN)) {
            velocity_y = tested_velocity;
            myFinalUpperLeftPosition = new Point(currPosition.getX() + velocity_x, currPosition.getY() + velocity_y);
        }

        return myFinalUpperLeftPosition;
    }

    private boolean collisionHappens(Point myTestedUpperRight, Point otherCarPosition) {
        if(direction.equals(Direction.X_RIGHT) && myTestedUpperRight.getX() >= otherCarPosition.getX()) {
            chanceForCollisionSameStreet = true;
            return true;
        }
        //możliwe, że będzie trzeba zamienić na <= w końcu poruszają sie w dół, jeśli y rosną w górę
        if(direction.equals(Direction.Y_DOWN) && myTestedUpperRight.getY() >= otherCarPosition.getY()) {
            chanceForCollisionSameStreet = true;
            return true;
        }
        return false;
    }

    private void performMove(Point positionAfterMove, ACLMessage msg) {
        System.out.println("Moving agent " + myAgent.getLocalName() + " from " + currPosition + " to " + positionAfterMove);
        myAgent.addBehaviour(new MoveCarBehaviour(myAgent, gui, positionAfterMove));
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(new CarMessage(myAgent.getLocalName(), positionAfterMove, streetNumber, velocity_x, velocity_y, direction).toString());
        myAgent.send(reply);
        currPosition = positionAfterMove;
        done = true;
        block();
    }

    private void normalDrive(ACLMessage msg) {
//        if(velocity_x < max_velocity_x) {
//            velocity_x += acceleration;
//        }
//        if(velocity_y < max_velocity_y) {
//            velocity_y += acceleration;
//        }

        Point positionAfterMove = new Point(currPosition.getX() + velocity_x, currPosition.getY() + velocity_y);
        performMove(positionAfterMove, msg);
    }

    private boolean chechIfCarIsBehind(Point myUpperLeft, Point otherCarUpperLeft) {
        if(direction.equals(Direction.X_RIGHT)) {
            return myUpperLeft.getX() < otherCarUpperLeft.getX();
        }
        return false;
    }
}
