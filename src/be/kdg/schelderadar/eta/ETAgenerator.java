package be.kdg.schelderadar.eta;

import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.domain.ship.Ship;

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
    private ETATime etaTime;

    public ETAgenerator(ETATime etaTime) {
        this.etaTime = etaTime;
    }

    public void setEtaTime(ETATime etaTime) {
        this.etaTime = etaTime;
    }

    public ETATime getEtaTime() {
        return etaTime;
    }


    public Map<Ship, Long> getETAs(Map<Ship, ArrayList<PositionMessage>> shipMap) {
         Map<Ship, Long> shipETAs = new TreeMap<>();
         for (Map.Entry<Ship, ArrayList<PositionMessage>> entry : shipMap.entrySet()) {
            if (entry.getValue().size() > 1) {
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

    private float calculateETA(int distance, float speedOfShip) {
        return distance / speedOfShip;
    }

}
