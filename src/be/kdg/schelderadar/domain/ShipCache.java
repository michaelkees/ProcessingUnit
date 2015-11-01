package be.kdg.schelderadar.domain;

import be.kdg.schelderadar.domain.model.ShipInfo;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ShipCache {
    private Map<Integer, ShipInfo> shipInfos;
    private long lastTimeCleared;
    private long timeSpanToCache;

    public ShipCache(long timeSpanToCache) {
        this.shipInfos = new TreeMap<>();
        this.lastTimeCleared = new Date().getTime();
        this.timeSpanToCache = timeSpanToCache;
    }

    public ShipInfo getShipInfo(int shipId) {
        return shipInfos.get(shipId);
    }

    public void cacheShipInfo(int shipId, ShipInfo shipInfo) {
        shipInfos.put(shipId, shipInfo);
    }

    public void checkCacheClear() {
        long currentTime = new Date().getTime();
        if(lastTimeCleared + timeSpanToCache < currentTime){
            clearCache();
        }
    }
    public void clearCache(){
        shipInfos.clear();
    }

    //TODO #keren contact leggen als service uitvalt -> INSTELBAAR

}
