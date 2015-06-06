package pl.edu.agh.agents.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import pl.edu.agh.agents.*;

import java.lang.Math;

import java.util.ArrayList;
import java.util.List;

public class DriverBehaviour extends Behaviour {
    public static int TIME_TO_COLLISION_THRESHOLD = 2;
    //private AgentConfiguration configuration;
    private Double currentPosition;
    private Integer velocity;
    private boolean done;
    private DriverAgent agent;

    public DriverBehaviour(Agent agent) {
        super(agent);
        this.agent = (DriverAgent)agent;
        currentPosition = this.agent.getConfiguration().getInitialPosition();
        velocity = this.agent.getConfiguration().getInitialVelocity();
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            //TODO: this logic is too complicated. Maybe we should handle somehow else WelcomeMessage
            if (!msg.getContent().startsWith("Welcome")) {
                List<CarMessage> driversInfo = MessageParser.getCarMessages(msg.getContent(), myAgent.getName());
                List<CarMessage> driversAhead = getDriversAhead(driversInfo);
                checkForCrossroadsNearby();
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
        return agent.getConfiguration().getStreetNumber() == message.getStreetNumber();
    }

    private boolean isAheadOfMe(CarMessage message) {
        return message.getCurrentPosition() > this.currentPosition;
    }

    private void simplyMove() {
        double distance;
        int newVelocity = Math.min(velocity + agent.getConfiguration().getAcceleration(), agent.getConfiguration().getMaxVelocity());
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
        return message.getCurrentPosition() - (this.currentPosition + agent.getConfiguration().getCarWidth());
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
        double distance = otherCarPosition - agent.getConfiguration().getCarWidth() - currentPosition;
        if (velocity != 0) {
            return (int) Math.round(distance / velocity);
        }
        return Integer.MAX_VALUE;
    }

    private void slowDown() {
        System.out.println("Slowing down!");
        double distance;
        int newVelocity = Math.max(velocity - agent.getConfiguration().getAcceleration(), 0);
        distance = (velocity + newVelocity) / 2;    // simple approximation of accelarated move
        currentPosition += distance;
        velocity = newVelocity;
    }

    private void replyWithNewPosition(ACLMessage msg) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(new CarMessage(myAgent.getLocalName(), currentPosition, agent.getConfiguration().getStreetNumber(), velocity, agent.getConfiguration().getCarWidth()).toString());
        System.out.println("Sending reply: " + reply.getContent());
        myAgent.send(reply);
        done = true;
        block();
    }

    private String checkForCrossroadsNearby() {
        int streetNumber = agent.getConfiguration().getStreetNumber();
        List<Crossroad> crossroadsNearby = agent.getGui().getCrossRoadsForGivenStreetNumber(streetNumber);
        Double position = currentPosition;
        Street street = agent.getStreet();
        double positionAhead = position;
        boolean crossroadFound = false;
        StringBuilder builder = new StringBuilder("");
        //maksymalnie bêdziemy wyszukiwaæ skrzy¿owania 100 metrów od bie¿¹cego po³o¿enia
        while(positionAhead <= position + 100 && !crossroadFound) {
            for(Crossroad crossroad : crossroadsNearby) {
                if(street.getDirection().equals(Direction.HORIZONTAL)) {
                    if(positionAhead >= crossroad.getUpperLeft().getX() && positionAhead <= crossroad.getBottomRight().getX()) {
                        crossroadFound = true;

                        //jeœli aktualnie rozwa¿ane auto jest na skrzy¿owaniu
                        if(positionAhead == position) {
                            crossroad.setIsCarOnCrossroad(true);
                        }
                        //w wiadomosci do konkretnego drivera bedziemy przesylac informacje o tym, jak daleko jest od najblizszego skrzyzowania i czy ktos aktualnie sie na nim znajduje
                        builder.append(positionAhead - position);
                        builder.append(";");
                        builder.append(crossroad.isCarOnCrossroad());
                    }
                }
                else {
                    if(positionAhead >= crossroad.getUpperLeft().getY() && positionAhead <= crossroad.getBottomRight().getY()) {
                        crossroadFound = true;

                        //jeœli aktualnie rozwa¿ane auto jest na skrzy¿owaniu
                        if(positionAhead == position) {
                            crossroad.setIsCarOnCrossroad(true);
                        }
                        //w wiadomosci do konkretnego drivera bedziemy przesylac informacje o tym, jak daleko jest od najblizszego skrzyzowania i czy ktos aktualnie sie na nim znajduje
                        builder.append(positionAhead - position);
                        builder.append(";");
                        builder.append(crossroad.isCarOnCrossroad());
                    }
                }
            }
            positionAhead += 10; // szukamy skrzy¿owania 10 metrów dalej
        }
        return builder.toString();
    }

    @Override
    public boolean done() {
        return false;
    }
}