package pl.edu.agh.agents.behaviours;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import pl.edu.agh.agents.*;

import java.util.List;

/**
 * Created by Mere on 2015-06-04.
 */
public class WorldControlBehaviour extends Behaviour {
    private boolean done = false;
    private int messageCount = 0;
    private SupervizorAgent supervizorAgent;

    @Override
    public void action() {
        //otrzymywanie wiadomosci od agentow o ich predkosci, polozeniu itp.
        ACLMessage msg = myAgent.receive();
        supervizorAgent = (SupervizorAgent)myAgent;

        if(msg != null) {
            messageCount++;
            String sender = msg.getSender().getName().split("@")[0];

            updateDriver(sender, msg);
            if(messageCount == supervizorAgent.drivers.size()) {
                messageCount = 0;

                String driversInfoMsg = createDriverMessage();
                if (checkForCollisions()){
                    supervizorAgent.gui.displayText("Collision detected!");
                    done = true;
                    return;
                }
                checkForCollisions();

                sendDriverMessage(driversInfoMsg, msg);

                try {
                    Thread.sleep(200);
                    System.out.println("Waiting for next round");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //TODO: refactor ASAP as well as whole class SupervizorAgent!
    private boolean checkForCollisions() {
        for (Street street : supervizorAgent.agentsOnStreet.keySet()){
            List<String> agents = supervizorAgent.agentsOnStreet.get(street);
            for (String agent : agents) {
                for (String otherAgent : agents) {
                    if (!agent.equals(otherAgent)) {
                        CarMessage message1 = MessageParser.parseCarMessage(supervizorAgent.driversInfoMap.get(agent));
                        CarMessage message2 = MessageParser.parseCarMessage(supervizorAgent.driversInfoMap.get(otherAgent));
                        if (collide(message1, message2)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean collide(CarMessage message1, CarMessage message2) {
        if (message1.getCurrentPosition() < message2.getCurrentPosition()) {
            return message1.getCurrentPosition() + message1.getCarWidth() >= message2.getCurrentPosition();
        } else {
            return message2.getCurrentPosition() + message1.getCarWidth() >= message1.getCurrentPosition();
        }
    }

    private void updateDriver(String sender, ACLMessage msg) {
        supervizorAgent.driversInfoMap.put(sender, msg.getContent());
        supervizorAgent.updateGui(msg.getContent(), msg.getSender());
    }

    private String createDriverMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Object value : supervizorAgent.driversInfoMap.values()) {
            stringBuilder.append(value);
            stringBuilder.append("/");
        }

        return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
    }

    private void sendDriverMessage(String driversInfoMsg, ACLMessage msg) {
        for (Driver driver : supervizorAgent.drivers) {
            ACLMessage broadcastMsg = new ACLMessage(ACLMessage.INFORM);
            broadcastMsg.setContent(driversInfoMsg);
            broadcastMsg.addReceiver(driver.getDriverAgentID());
            myAgent.send(broadcastMsg);
        }
    }

    private String checkForCrossroadsNerby(ACLMessage msg) {
        List<CarMessage> carMessageList = MessageParser.getCarMessages(msg.getContent(), myAgent.getName());
        int streetNumber = carMessageList.get(0).getStreetNumber();
        List<Crossroad> crossroadsNearby = supervizorAgent.gui.getCrossRoadsForGivenStreetNumber(streetNumber);
        Double position = carMessageList.get(0).getCurrentPosition();
        Street street = supervizorAgent.getStreetByNumber(streetNumber);
        double positionAhead = position;
        boolean crossroadFound = false;
        StringBuilder builder = new StringBuilder("");
        //maksymalnie bêdziemy wyszukiwaæ skrzy¿owania 100 metrów od bie¿¹cego po³o¿enia
        while(positionAhead <= 100 && !crossroadFound) {
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
                        builder.append("-");
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
                        builder.append("-");
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
        return done;
    }
}
