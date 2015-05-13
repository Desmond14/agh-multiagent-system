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
                Point myPredictedUpperRightPosition = null;
                for (CarMessage otherCarInfo : driversInfo) {
                    if (!otherCarInfo.getDriverName().equals(myAgent.getLocalName()) && otherCarInfo.getStreetNumber() == streetNumber
                            && checkIfCarIsBehind(currPosition, otherCarInfo.getCarPosition())) {
                        //jeśli dwaj kierowcy znajdują się na jednej ulicy, to nie mogą w siebie wjeżdżać

                        if(myPredictedUpperRightPosition == null) {
                            if (velocity_x < max_velocity_x && direction.equals(Direction.X_RIGHT)) {
                                myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x + acceleration + car.getWidth(),
                                        currPosition.getY() + velocity_y);
                            }
                            else if (velocity_y < max_velocity_y && direction.equals(Direction.Y_DOWN)) {
                                myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x,
                                        currPosition.getY() + velocity_y + acceleration + car.getWidth());
                            } else if (velocity_x == max_velocity_x && direction.equals(Direction.X_RIGHT)) {
                                myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x + car.getWidth(),
                                        currPosition.getY() + velocity_y);
                            }
                            else if (velocity_y == max_velocity_y && direction.equals(Direction.Y_DOWN)) {
                                myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x,
                                        currPosition.getY() + velocity_y + car.getWidth());
                            }
                        }
                        //Point myPredictedUpperRightPosition = null;


                        positionAfterMove = avoidCollision(myPredictedUpperRightPosition, otherCarInfo);
                    }
                    else if(!otherCarInfo.getDriverName().equals(myAgent.getLocalName()) && otherCarInfo.getStreetNumber() != streetNumber
                            && checkIfCarIsBehind(currPosition, otherCarInfo.getCarPosition())) {

//                        if (velocity_x < max_velocity_x && direction.equals(Direction.X_RIGHT)) {
//                            myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x + acceleration + car.getWidth(),
//                                    currPosition.getY() + velocity_y);
//                        }
//                        else if (velocity_y < max_velocity_y && direction.equals(Direction.Y_DOWN)) {
//                            myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x,
//                                    currPosition.getY() + velocity_y + acceleration + car.getWidth());
//                        } else if (velocity_x >= max_velocity_x && direction.equals(Direction.X_RIGHT)) {
//                            myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x + car.getWidth(),
//                                    currPosition.getY() + velocity_y);
//                        }
//                        else if (velocity_y == max_velocity_y && direction.equals(Direction.Y_DOWN)) {
//                            myPredictedUpperRightPosition = new Point(currPosition.getX() + velocity_x,
//                                    currPosition.getY() + velocity_y + car.getWidth());
//                        }
                    }
                }
            }


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

    public Point avoidCollision(Point myUpperRight, CarMessage otherCarInfo) {
        int tested_velocity = 0;
        if(direction.equals(Direction.X_RIGHT)) {
            if(max_velocity_x - velocity_x >= acceleration) {
                tested_velocity = velocity_x + acceleration;
            }
            else {
                tested_velocity = max_velocity_x;
            }

        }
        else if(direction.equals(Direction.Y_DOWN)) {
            if(max_velocity_y - velocity_y >= acceleration) {
                tested_velocity = velocity_y + acceleration;
            }
            else {
                tested_velocity = max_velocity_y;
            }
        }
        Point myTestedUpperRightPosition = myUpperRight;
        Point myFinalUpperLeftPosition = null;
        while(collisionHappens(myTestedUpperRightPosition, otherCarInfo.getCarPosition()) && tested_velocity >= 0) {

            if(direction.equals(Direction.X_RIGHT)) {
                myTestedUpperRightPosition = new Point(currPosition.getX() + tested_velocity + car.getWidth(),
                        currPosition.getY() + velocity_y);
            }
            else if(direction.equals(Direction.Y_DOWN)) {
                myTestedUpperRightPosition = new Point(currPosition.getX() + velocity_x,
                        currPosition.getY() + tested_velocity + car.getWidth());
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

    private boolean checkIfCarIsBehind(Point myUpperLeft, Point otherCarUpperLeft) {
        if(direction.equals(Direction.X_RIGHT)) {
            return myUpperLeft.getX() < otherCarUpperLeft.getX();
        }
        else if(direction.equals(Direction.Y_DOWN)) {
            return myUpperLeft.getY() < otherCarUpperLeft.getY();
        }
        return false;
    }
}
