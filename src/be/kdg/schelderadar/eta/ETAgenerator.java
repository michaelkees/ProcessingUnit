package be.kdg.schelderadar.eta;

import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.domain.ship.Ship;

import java.util.*;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ETAgenerator {
    private ETATime etaTime;

    public ETAgenerator(ETATime etaTime) {
        this.etaTime = etaTime;
    }

    public ETATime getEtaTime() {
        return etaTime;
    }

    public void setEtaTime(ETATime etaTime) {
        this.etaTime = etaTime;
    }

    public NavigableMap<Ship, Long> getETAs(Map<Ship, ArrayList<PositionMessage>> shipMap) {
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

    public Boolean analyzeSpeedShip(NavigableMap<Ship, ArrayList<PositionMessage>> shipMap){
        NavigableMap<Ship, Long> shipETAs = getETAs(shipMap);
        Map.Entry<Ship, Long> lastEntry = shipETAs.lastEntry();
        return lastEntry.getValue() != 0;
    }

    private float calculateETA(int distance, float speedOfShip) {
        return distance / speedOfShip;
    }

}
