package be.kdg.schelderadar.domain;

import be.kdg.schelderadar.broker.MessageQueue;
import be.kdg.schelderadar.broker.MQException;
import be.kdg.schelderadar.cache.PositionShipCache;
import be.kdg.schelderadar.cache.ShipInfoCache;
import be.kdg.schelderadar.domain.message.IncidentMessage;
import be.kdg.schelderadar.domain.message.ShipMessageCollector;
import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.domain.ship.Ship;
import be.kdg.schelderadar.domain.ship.ShipInfo;
import be.kdg.schelderadar.eta.ETAReport;
import be.kdg.schelderadar.eta.ETATime;
import be.kdg.schelderadar.eta.ETAgenerator;
import be.kdg.schelderadar.out.store.MessageStorage;
import be.kdg.schelderadar.service.ShipService;
import be.kdg.schelderadar.service.ShipServiceException;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Controller class
 */

public class ProcessingUnit {
    private int timeToInterrupt = 100000; //DEFAULT
    private MessageQueue messageQueue;
    private ShipService shipService;
    private boolean isReceiving;

    private ShipMessageCollector msgCollector;
    private ETAgenerator etaGenerator;

    private MessageStorage msgStorage;

    //collectors/cache classes
    private final ShipBuffer shipBuffer;
    private ShipInfoCache shipInfoCache;
    private PositionShipCache positionShipCache;

    public ProcessingUnit(ShipMessageCollector msgCollector, MessageStorage storage) {
        this.shipBuffer = new ShipBuffer(timeToInterrupt);
        this.isReceiving = false;
        this.msgCollector = msgCollector;
        this.etaGenerator = new ETAgenerator(ETATime.NORMAL); //DEFAULT
        this.positionShipCache = new PositionShipCache();
        this.msgStorage = storage;
    }

    public void setEtaTime(ETATime etaTime) {
        this.etaGenerator.setEtaTime(etaTime);
    }

    public void setShipInfoCache(ShipInfoCache shipInfoCache) {
        this.shipInfoCache = shipInfoCache;
    }

    public void setTimeToInterrupt(int timeToInterrupt) {
        this.timeToInterrupt = timeToInterrupt;
    }

    public void setMessageQueue(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    public void start() throws MQException, ShipServiceException {
        isReceiving = true;
        while (isReceiving) {
            messageQueue.init();
            performCollect();
            checkBufferedShipSignal();
            checkCacheClear();
            checkETAgeneration(null);
            msgCollector.clear();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        isReceiving = true;
    }

    private void checkETAgeneration(ArrayList<Integer> shipIds) {
        switch (etaGenerator.getEtaTime()){
            case NORMAL:
                    /*if(!shipBuffer.getShipList().isEmpty()){
                        if(!shipIds.isEmpty() && reportETAs(shipIds)!=null){
                            msgStorage.saveMessage(reportETAs(shipIds));
                        }
                    }*/
                break;
            case POSITION:
                break;
            case ZONE:

        }

    }

    public ETAReport reportETAs(ArrayList<Integer> shipIds){
        ETAReport report = new ETAReport();
        Map<Ship, ArrayList<PositionMessage>> shipMap = new TreeMap<>();
        for(Integer shipId : shipIds){
            if(shipBuffer.exitsShip(shipId)){
                Ship s = shipBuffer.getShip(shipId);
                shipMap.put(s, positionShipCache.getPosMessages().get(s));
            }
        }
        if(!shipMap.isEmpty()){
            Map<Ship, Long> shipETAs = etaGenerator.getETAs(shipMap);
            if(!shipETAs.isEmpty()) {
                for (Map.Entry<Ship, Long> entry : shipETAs.entrySet()) {
                    report.addShipETAData(entry.getKey(), entry.getValue());
                }
                return report;
            }
        }
        return null;
    }

    private void checkCacheClear() {
        shipInfoCache.checkCacheClear();
    }

    private void checkBufferedShipSignal() {
        shipBuffer.checkSignalShip();
    }

    public void performCollect() throws ShipServiceException {
        for (PositionMessage ps : msgCollector.getPositionMessages()) {
            bufferShip(ps);
            msgStorage.saveMessage(ps);
        }
        for(IncidentMessage im : msgCollector.getIncidentMessages()){
            reportIncident(im);
            msgStorage.saveMessage(im);
        }
    }

    private void reportIncident(IncidentMessage im) {
        //TODO incidentMessage afhandeling
    }

    public void bufferShip(PositionMessage ps) throws ShipServiceException {
        //if ship already exists
        if (shipBuffer.exitsShip(ps.getShipId())) {
            Ship shipOnRadar = shipBuffer.getShip(ps.getShipId());
            shipBuffer.updateShip(shipOnRadar, ps.getAfstandTotLoskade());
        } else {
            Ship ship = new Ship(ps.getShipId(), ps.getCentraleId(), ps.getTimestamp(), ps.getAfstandTotLoskade());
            ship.setShipInfo(collectShipInfo(ship.getShipId()));
            shipBuffer.addShip(ship);
        }
        positionShipCache.addPosMessage(shipBuffer.getShip(ps.getShipId()), ps); //USE ETA GENERATOR
    }

    public ShipInfo collectShipInfo(int shipId) throws ShipServiceException {
        ShipInfo shipInfo = shipService.getShipInfo(shipId);
        shipInfoCache.cacheShipInfo(shipId, shipInfo);
        return shipInfo;
    }
}
