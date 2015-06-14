package pl.edu.agh.agents.behaviours;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import pl.edu.agh.agents.*;
import pl.edu.agh.agents.gui.Main;

import java.util.ArrayList;
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
                if (checkForCollisionsOnStreet() || checkForCollisionsOnCrossroads()){
                    supervizorAgent.gui.displayText("Collision detected!");
                    done = true;
                    return;
                }
                checkForCollisionsOnStreet();
                checkForCollisionsOnCrossroads();

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
    private boolean checkForCollisionsOnStreet() {
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

    private boolean checkForCollisionsOnCrossroads() {
        List<String> agents = new ArrayList<>();
        for (Street street : supervizorAgent.agentsOnStreet.keySet()){
            agents.addAll(supervizorAgent.agentsOnStreet.get(street));
        }
        for (String agent : agents) {
            for (String otherAgent : agents) {
                if (!agent.equals(otherAgent)) {
                    CarMessage message1 = MessageParser.parseCarMessage(supervizorAgent.driversInfoMap.get(agent));
                    CarMessage message2 = MessageParser.parseCarMessage(supervizorAgent.driversInfoMap.get(otherAgent));
                    if (message1.getStreetNumber() != message2.getStreetNumber()) {
                        Direction dir1 = supervizorAgent.getStreetByNumber(message1.getStreetNumber()).getDirection();
                        if (dir1 == Direction.HORIZONTAL) {
                            for (Crossroad crossroad : supervizorAgent.gui.getCrossRoadsForGivenStreetNumber(message1.getStreetNumber())) {
                                if (message1.getCurrentPosition() + message1.getCarWidth() >= crossroad.getUpperLeft().getX() && message1.getCurrentPosition() <= crossroad.getBottomRight().getX()
                                        && message2.getCurrentPosition() + message2.getCarWidth() >= crossroad.getUpperLeft().getY() && message2.getCurrentPosition() <= crossroad.getBottomRight().getY()) {
                                    //they're both on the crossroad but do they collide?
                                    if (message1.getCurrentPosition() + message1.getCarWidth() > (Main.STAGE_WIDTH - message2.getCarWidth()) /2) {
                                        System.out.println("Collision between " + message1.getDriverName() + " and " + message2.getDriverName());
                                        return true;
                                    }
                                }
                            }
                        } else {
                            for (Crossroad crossroad : supervizorAgent.gui.getCrossRoadsForGivenStreetNumber(message1.getStreetNumber())) {
                                if (message1.getCurrentPosition() >= crossroad.getUpperLeft().getY() && message1.getCurrentPosition() <= crossroad.getBottomRight().getY()
                                        && message2.getCurrentPosition() >= crossroad.getUpperLeft().getX() && message2.getCurrentPosition() <= crossroad.getBottomRight().getX()) {
                                    System.out.println("Collision between " + message1.getDriverName() + " and " + message2.getDriverName());
                                    return true;
                                }
                            }
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

    @Override
    public boolean done() {
        return done;
    }
}
