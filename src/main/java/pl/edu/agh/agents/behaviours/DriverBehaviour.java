package pl.edu.agh.agents.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import pl.edu.agh.agents.CarMessage;
import pl.edu.agh.agents.MessageParser;
import pl.edu.agh.agents.gui.Point;
import java.lang.Math;

import java.util.ArrayList;
import java.util.List;

public class DriverBehaviour extends Behaviour {
    public static int TIME_TO_COLLISION_THRESHOLD = 2;
    private int velocity;
    private int maxVelocity;
    private int acceleration;
    private double currentPosition;
    private int streetNumber;
    private int carWidth;
    private boolean done;

    public DriverBehaviour(Agent agent, Object[] args) {
        super(agent);
        this.carWidth = (Integer) args[0];
        this.velocity = (Integer) args[1];
        this.maxVelocity = (Integer) args[3];
        this.currentPosition = (Double) args[5];
        this.streetNumber = (Integer) args[6];
        this.acceleration = (Integer) args[8];
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            //TODO: this logic is too complicated. Maybe we should handle somehow else WelcomeMessage
            if (!msg.getContent().startsWith("Welcome")) {
                List<CarMessage> driversInfo = MessageParser.getCarMessages(msg.getContent(), myAgent.getName());
                List<CarMessage> driversAhead = getDriversAhead(driversInfo);
                if (driversAhead.isEmpty()){
                    simplyMove();
                } else {
                    avoidCollisionWith(getClosest(driversAhead));
                }
            } else {
                simplyMove();
            }
            replyWithNewPosition(msg);
        }
    }

    private List<CarMessage> getDriversAhead(List<CarMessage> driversInfo) {
        List<CarMessage> driversAhead = new ArrayList<>();
        for (CarMessage message : driversInfo){
            if (isOnSameStreet(message) && isAheadOfMe(message)){
                driversAhead.add(message);
            }
        }
        return driversAhead;
    }

    private boolean isOnSameStreet(CarMessage message) {
        return this.streetNumber == message.getStreetNumber();
    }

    private boolean isAheadOfMe(CarMessage message) {
        return message.getCurrentPosition() > this.currentPosition;
    }

    private void simplyMove() {
        double distance;
        int newVelocity = Math.min(velocity + acceleration, maxVelocity);
        distance = (velocity + newVelocity) / 2;    // simple approximation of accelarated move
        currentPosition += distance;
        velocity = newVelocity;
    }

    private CarMessage getClosest(List<CarMessage> driversAhead) {
        CarMessage closestDriver = null;
        double distanceToClosest = 0.0;
        for (CarMessage message : driversAhead){
            double distanceTo = calculateDistance(message);
            if (closestDriver == null || distanceTo < distanceToClosest){
                closestDriver = message;
                distanceToClosest = distanceTo;
            }
        }
        return closestDriver;
    }

    private double calculateDistance(CarMessage message) {
        return message.getCurrentPosition() - (this.currentPosition + this.carWidth);
    }

    private void avoidCollisionWith(CarMessage closestCar) {
        int timeToCollision = calculateTimeToCollision(closestCar);
        if (timeToCollision < TIME_TO_COLLISION_THRESHOLD){
            slowDown();
        } else {
            simplyMove();
        }
    }

    private int calculateTimeToCollision(CarMessage closestCar) {
        double otherCarPosition = closestCar.getCurrentPosition();
        double distance = otherCarPosition - carWidth - currentPosition;
        if (velocity != 0) {
            return (int) Math.round(distance / velocity);
        }
        return Integer.MAX_VALUE;
    }

    private void slowDown() {
        System.out.println("Slowing down!");
        double distance;
        int newVelocity = Math.max(velocity - acceleration, 0);
        distance = (velocity + newVelocity) / 2;    // simple approximation of accelarated move
        currentPosition += distance;
        velocity = newVelocity;
    }

    private void replyWithNewPosition(ACLMessage msg) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(new CarMessage(myAgent.getLocalName(), currentPosition, streetNumber, velocity).toString());
        System.out.println("Sending reply: " + reply.getContent());
        myAgent.send(reply);
        done = true;
        block();
    }

    @Override
    public boolean done() {
        return false;
    }

    private void performMove(Point positionAfterMove, ACLMessage msg) {
        System.out.println("Moving agent " + myAgent.getLocalName() + " from " + currentPosition + " to " + positionAfterMove);
//        myAgent.addBehaviour(new MoveCarBehaviour(myAgent, gui, positionAfterMove));
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.INFORM);
//        reply.setContent(new CarMessage(myAgent.getLocalName(), positionAfterMove, streetNumber, velocity, velocity_y, direction).toString());
        myAgent.send(reply);
//        currentPosition = positionAfterMove;
    }
}
