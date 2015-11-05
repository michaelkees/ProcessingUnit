package be.kdg.schelderadar.cache;

import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.domain.ship.Ship;

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
        ArrayList<PositionMessage> psMsgs;
       if(!posMessages.isEmpty()){
           for (Map.Entry<Ship, ArrayList<PositionMessage>> entry : posMessages.entrySet()) {
               if (entry.getKey().getShipId() == ship.getShipId()) {

                   if (entry.getValue() != null) {
                       psMsgs = entry.getValue();
                   } else {
                       psMsgs = new ArrayList<>();
                   }
                   psMsgs.add(ps);
                   entry.setValue(psMsgs);
               }

           }
       } else {
           posMessages = new TreeMap<>();
           psMsgs = new ArrayList<>();
           psMsgs.add(ps);
           posMessages.put(ship, psMsgs);
       }
    }

    public NavigableMap<Ship, ArrayList<PositionMessage>> getPosMessages() {
        return posMessages;
    }
}
