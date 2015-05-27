package pl.edu.agh.agents;

import java.util.ArrayList;
import java.util.List;

public class MessageParser {

    public static List<CarMessage> getCarMessages(String message, String agentName) {
        List<CarMessage> carMessages = new ArrayList<CarMessage>();
        String[] driversInfo = message.split("/");
        for(String driver : driversInfo) {
            CarMessage carMessage = parseCarMessage(driver);
            if (!agentName.equals(carMessage.getDriverName())) {
                carMessages.add(carMessage);
            }
        }
        return carMessages;
    }

    public static CarMessage parseCarMessage(String message) {
        String[] messageChunks = message.split(";");
        return new CarMessage(
                messageChunks[0],
                Double.parseDouble(messageChunks[1]),
                Integer.parseInt(messageChunks[2]),
                Integer.parseInt(messageChunks[3]),
                Double.parseDouble(messageChunks[4])
        );
    }
}
