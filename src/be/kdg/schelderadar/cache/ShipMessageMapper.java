package be.kdg.schelderadar.cache;

import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.domain.ship.Ship;
import be.kdg.schelderadar.domain.ship.ShipInfo;

import java.util.*;

/**
 * User: michaelkees
 * Date: 02/11/15
 */
public class ShipMessageMapper {
    private Map<Integer, ShipInfo> shipInfos;
    private NavigableMap<Ship, ArrayList<PositionMessage>> shipPositionMessages = new TreeMap<>();
    private long lastTimeCleared;
    private long timeSpanToCache;

    public ShipMessageMapper(long timeSpanToCache) {
        this.lastTimeCleared = new Date().getTime();
        this.timeSpanToCache = timeSpanToCache;
    }

    public ShipInfo getShipInfo(int shipId) {
        for(Map.Entry<Ship, ArrayList<PositionMessage>> shipMap : shipPositionMessages.entrySet()){
            if(shipMap.getKey().getShipId() == shipId){
                if(shipMap.getKey().getShipInfo()!=null){
                    return shipMap.getKey().getShipInfo();
                }
            }
        }
        return null;
    }

    public void addPosMessage(Ship ship, PositionMessage ps) {
        boolean shipFound = false;
        ArrayList<PositionMessage> posMsgs = new ArrayList<>();

        for (Map.Entry<Ship, ArrayList<PositionMessage>> shipWithPositionMessages : shipPositionMessages.entrySet()) {
            if (!shipWithPositionMessages.getKey().equals(ship)) {
                shipFound = false;
            } else {
                posMsgs = shipWithPositionMessages.getValue();
                shipFound = true;
            }
        }
        if (!shipFound) {
            posMsgs.add(ps);
            shipPositionMessages.put(ship, posMsgs);
        } else {
            shipPositionMessages.get(ship).add(ps);
        }
    }

    public void checkCacheClear() {
        long currentTime = new Date().getTime();
        if(currentTime >= lastTimeCleared + timeSpanToCache){
            clearCache();
            this.lastTimeCleared = currentTime;
        }
    }

    //CLEARS CACHE OF FIRST POSITION MESSAGES
    public void clearCache(){
        for(Map.Entry<Ship, ArrayList<PositionMessage>> shipMap : shipPositionMessages.entrySet()){
            int sizeShipMap = shipMap.getValue().size();
            if(sizeShipMap>5){
                for(int i = 0; i < 2; i++){
                    shipMap.getValue().remove(i); //remove first values of arraylist
                }
            }
        }
    }

    public NavigableMap<Ship, ArrayList<PositionMessage>> getShipPositionMessages() {
        return shipPositionMessages;
    }
}
