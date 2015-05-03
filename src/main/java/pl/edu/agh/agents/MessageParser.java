package pl.edu.agh.agents;

import pl.edu.agh.agents.gui.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S�awek on 2015-05-02.
 */
public class MessageParser {

    public static List<CarMessage> getCarMessages(String message, String agentName) {
        List<CarMessage> carMessages = new ArrayList<CarMessage>();
        String[] driversInfo = message.split("/");
        for(String driver : driversInfo) {
            String[] info = driver.split(";");
            if(!info[0].equals(agentName)) {
                carMessages.add(new CarMessage(
                        info[0],
                        new Point(Double.parseDouble(info[1]), Double.parseDouble(info[2])),
                        Integer.parseInt(info[3]),
                        Integer.parseInt(info[4]),
                        Integer.parseInt(info[5])
                ));
            }
        }
        return carMessages;
    }
}
