package be.kdg.schelderadar.domain;

import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.domain.model.Ship;

import java.util.*;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ETAgenerator {
    private ArrayList<Date> ETAShips = new ArrayList<>();
    //private ArrayList<Ship> shipsForETAcalc = new ArrayList<>();

    //TODO: Hierbij moet kunnen ingesteld worden voor welke schepen men ETA’s wenst en
    //of ze moeten gegenereerd worden voor elk nieuw ontvangen positiebericht of
    //enkel wanneer een schip een nieuwe zone betreedt.
    // Het betreden van een zone kan afgeleid worden uit een wijziging in het CentraleID.

    public ETAgenerator() {

    }

    public Map<Ship, Long> getETAs(Map<Ship, List<PositionMessage>> shipPositionMap){
        Map<Ship, Long> shipETAs = new TreeMap<>();

        for (Map.Entry<Ship, List<PositionMessage>> entry : shipPositionMap.entrySet()) {
            PositionMessage psOld = entry.getValue().get(0);
            PositionMessage psCurrent = entry.getValue().get(1);
            long difDistance = psOld.getAfstandTotLoskade()/psCurrent.getAfstandTotLoskade();
            long difTime =  psOld.getTimestamp().getTime()/psCurrent.getTimestamp().getTime();
            long speed = difDistance/difTime;
            long time = calculateETA(entry.getKey().getAfstandTotLoskade(), speed);

            shipETAs.put(entry.getKey(), time);
        }
        return shipETAs;
    }

    private long calculateETA(int distance, long speedOfShip) {
        return distance/speedOfShip;
    }

}
