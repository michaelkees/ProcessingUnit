package be.kdg.schelderadar.cache;

import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.domain.ship.Ship;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * User: michaelkees
 * Date: 02/11/15
 */
public class PositionShipCache {
    private NavigableMap<Ship, ArrayList<PositionMessage>> posMessages = new TreeMap<>();

    public PositionShipCache() {
    }

    public void addPosMessage(Ship ship, PositionMessage ps) {
        boolean shipFound = false;
        ArrayList<PositionMessage> posMsgs = new ArrayList<>();

        for (Map.Entry<Ship, ArrayList<PositionMessage>> shipWithPositionMessages : posMessages.entrySet()) {
            if (!shipWithPositionMessages.getKey().equals(ship)) {
                shipFound = false;
            } else {
                posMsgs = shipWithPositionMessages.getValue();
                shipFound = true;
            }
        }
        if (!shipFound) {
            posMsgs.add(ps);
            posMessages.put(ship, posMsgs);
        } else {
            posMessages.get(ship).add(ps);
        }
    }

    public NavigableMap<Ship, ArrayList<PositionMessage>> getPosMessages() {
        return posMessages;
    }
}
