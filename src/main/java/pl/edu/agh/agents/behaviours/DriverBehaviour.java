package pl.edu.agh.agents.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import pl.edu.agh.agents.*;

import java.lang.Math;

import java.util.ArrayList;
import java.util.DuplicateFormatFlagsException;
import java.util.List;

public class DriverBehaviour extends Behaviour {
    public static int TIME_TO_COLLISION_THRESHOLD = 2;
    public static Double CROSSROAD_LENGTH = 50.0;
    private Double currentPosition;
    private Double distanceToCrossroad;
    private Integer velocity;
    private boolean done;
    private DriverAgent agent;

    public DriverBehaviour(Agent agent, Double distanceToCrossroad) {
        super(agent);
        this.agent = (DriverAgent)agent;
        currentPosition = this.agent.getConfiguration().getInitialPosition();
        velocity = this.agent.getConfiguration().getInitialVelocity();
        this.distanceToCrossroad = distanceToCrossroad;
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
                if (driversAhead.isEmpty()) {
                    if (distanceToCrossroad >= 0) {
                        avoidCollisionOnCrossroad(driversInfo);
                    } else{
                        simplyMove();
                    }
                } else if ((distanceToCrossroad >= 0 && distanceToCrossroad < calculateDistance(getClosest(driversAhead)))){
                    avoidCollisionOnCrossroad(driversInfo);
                }
                else {
                    if (!slowDownIfNecessary(driversInfo)) {
                        avoidCollisionWith(getClosest(driversAhead));
                    }
                }
            } else {
                simplyMove();
            }
            replyWithNewPosition(msg);
        }
    }

    private boolean slowDownIfNecessary(List<CarMessage> driversInfo) {
        if (calculateTimeToStop() <= agent.getConfiguration().getTimeToStopThreshold()){
            CarMessage closest = getClosestOnDifferentStreet(driversInfo);
            if (closest != null && !iWillBeFirst(closest)) {
                slowDown();
                return true;
            }
        }
        return false;
    }

    private void avoidCollisionOnCrossroad(List<CarMessage> driversInfo) {
        System.out.println(agent.getLocalName() + " avoiding collision on crossroad");
        if (calculateTimeToStop() > agent.getConfiguration().getTimeToStopThreshold()){
            System.out.println(agent.getLocalName() + " simply moving because threshold lower than needed: " + distanceToCrossroad);
            simplyMove();
        } else {
            CarMessage closest = getClosestOnDifferentStreet(driversInfo);
            if (closest == null || iWillBeFirst(closest)) {
                System.out.println(agent.getLocalName() + " I will be first");
                simplyMove();
            } else {
                slowDown();
            }
        }
    }

    private boolean iWillBeFirst(CarMessage closest) {
        double safetyDistance = agent.getConfiguration().getSafetyDistance();
        double myTimeToStreet = (distanceToCrossroad + agent.getConfiguration().getSafetyDistance()) / velocity;
        double otherTimeToStreet = closest.getDistanceToCrossroad() / closest.getVelocity();
        return myTimeToStreet < otherTimeToStreet;
    }

    private CarMessage getClosestOnDifferentStreet(List<CarMessage> driversInfo) {
        CarMessage closest = null;
        double closestDistanceToCrossroad = Double.MAX_VALUE;
        for (CarMessage driver : driversInfo){
            if (driver.getStreetNumber() != agent.getConfiguration().getStreetNumber()) {
                double distanceToCrossroad = driver.getDistanceToCrossroad();
                if (distanceToCrossroad > -20 && distanceToCrossroad < closestDistanceToCrossroad) {
                    closest = driver;
                }
            }
        }
        return closest;
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
        System.out.println(agent.getLocalName() + " simply moving");
        double distance;
        int newVelocity = Math.min(velocity + agent.getConfiguration().getAcceleration(), agent.getConfiguration().getMaxVelocity());
        distance = (velocity + newVelocity) / 2;    // simple approximation of accelarated move
        currentPosition += distance;
        distanceToCrossroad -= distance;
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

    private Double calculateTimeToStop(){
        return distanceToCrossroad / velocity;
    }

    private void avoidCollisionWith(CarMessage closestCar) {
        System.out.println(agent.getLocalName() + " avoiding collision with driver ahead");
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
        double distance;
        int newVelocity = Math.max(velocity - agent.getConfiguration().getAcceleration(), 0);
        distance = (velocity + newVelocity) / 2;    // simple approximation of accelarated move
        currentPosition += distance;
        distanceToCrossroad -= distance;
        velocity = newVelocity;
        System.out.println(agent.getLocalName() + " slowing down! Current velocity: " + velocity);
    }

    private void replyWithNewPosition(ACLMessage msg) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(new CarMessage(myAgent.getLocalName(),
                currentPosition,
                agent.getConfiguration().getStreetNumber(),
                velocity,
                agent.getConfiguration().getCarWidth(),
                distanceToCrossroad).toString());
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