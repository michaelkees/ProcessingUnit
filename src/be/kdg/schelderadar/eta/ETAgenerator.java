package be.kdg.schelderadar.eta;

import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.domain.ship.Ship;

import java.util.*;

/**
 * User: michaelkees
 * Date: 31/10/15
 */

//TODO: interface creeren ,
// later andere opties mogelijk
public class ETAGenerator implements ETACaller {

    @Override
    public NavigableMap<Ship, Long> calculateEstimatedTimeOfArrivalsMap(Map<Ship, ArrayList<PositionMessage>> shipMap) {
         NavigableMap<Ship, Long> shipETAs = new TreeMap<>();
         for (Map.Entry<Ship, ArrayList<PositionMessage>> entry : shipMap.entrySet()) {
            if ((!entry.getValue().isEmpty()) && entry.getValue().size() > 1) {
                PositionMessage psOld = entry.getValue().get(entry.getValue().size()-2);
                PositionMessage psCurrent = entry.getValue().get(entry.getValue().size()-1);
                float difDistance = psOld.getAfstandTotLoskade() - psCurrent.getAfstandTotLoskade();
                float difTime = ((psCurrent.getTimestamp().getTime() - psOld.getTimestamp().getTime())/1000);
                float speed = difDistance / difTime;
                long time = (long)calculateETA(psCurrent.getAfstandTotLoskade(), speed);
                shipETAs.put(entry.getKey(), time);
            }
        }
        return shipETAs;
    }

    @Override
    public Boolean analyzeSpeedShip(NavigableMap<Ship, ArrayList<PositionMessage>> shipMap){
        NavigableMap<Ship, Long> shipETAs = calculateEstimatedTimeOfArrivalsMap(shipMap);
        Map.Entry<Ship, Long> lastEntry = shipETAs.lastEntry();
        return lastEntry.getValue() != 0;
    }


    //
    private float calculateETA(int distance, float speedOfShip) {
        return distance / speedOfShip;
    }

}
