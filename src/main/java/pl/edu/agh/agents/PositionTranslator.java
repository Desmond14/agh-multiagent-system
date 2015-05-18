package pl.edu.agh.agents;

import jade.core.AID;
import pl.edu.agh.agents.gui.Point;

import java.util.Map;

public class PositionTranslator {
    private final Map<AID, Street> agentToStreet;

    public PositionTranslator(Map<AID, Street> agentToStreet) {
        this.agentToStreet = agentToStreet;
    }

    public Point translatePosition(AID aid, Double positionIn1D){
        Street agentStreet = agentToStreet.get(aid);
        Point guiPosition;
        //TODO: simple assumption about street coordinate used, should be more configurable
        if (Direction.HORIZONTAL.equals(agentStreet.getDirection())) {
            guiPosition = new Point(positionIn1D, 280);
        } else {
            guiPosition = new Point(380, positionIn1D);
        }
        return guiPosition;
    }
}
