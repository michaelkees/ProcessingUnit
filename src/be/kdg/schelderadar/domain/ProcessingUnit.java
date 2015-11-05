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
import be.kdg.schelderadar.out.report.IncidentReport;
import be.kdg.schelderadar.out.store.MessageStorage;
import be.kdg.schelderadar.service.ShipService;
import be.kdg.schelderadar.service.ShipServiceException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;
import java.util.*;

/**
 * Controller class
 */

public class ProcessingUnit {
    private int timeToInterrupt = 100000; //DEFAULT
    private MessageQueue inMessageQueue;
    private MessageQueue outMessageQueue;

    private ShipService shipService;
    private boolean isReceiving;
    private boolean incidentAlert;

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
        this.incidentAlert = false;
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

    public void setInMessageQueue(MessageQueue inMessageQueue) {
        this.inMessageQueue = inMessageQueue;
    }

    public void setOutMessageQueue(MessageQueue outMessageQueue) {
        this.outMessageQueue = outMessageQueue;
    }

    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    public void start() throws MQException, ShipServiceException, MarshalException, IOException, ValidationException {
        isReceiving = true;
        while (isReceiving) {
            inMessageQueue.init();

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
        switch (etaGenerator.getEtaTime()) {
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

    public ETAReport reportETAs(ArrayList<Integer> shipIds) {
        ETAReport report = new ETAReport();
        Map<Ship, ArrayList<PositionMessage>> shipMap = new TreeMap<>();
        for (Integer shipId : shipIds) {
            if (shipBuffer.exitsShip(shipId)) {
                Ship s = shipBuffer.getShip(shipId);
                shipMap.put(s, positionShipCache.getPosMessages().get(s));
            }
        }
        if (!shipMap.isEmpty()) {
            Map<Ship, Long> shipETAs = etaGenerator.getETAs(shipMap);
            if (!shipETAs.isEmpty()) {
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

    public void performCollect() throws ShipServiceException, MarshalException, IOException, ValidationException {
        if (!msgCollector.getPositionMessages().isEmpty()) {
            for (PositionMessage ps : msgCollector.getPositionMessages()) {
                bufferShip(ps);
                msgStorage.saveMessage(ps, PositionMessage.class.getSimpleName());
            }
        }
        if (!msgCollector.getIncidentMessages().isEmpty()) {
            for (IncidentMessage im : msgCollector.getIncidentMessages()) {
                if(im.getType().equals("alles normaal")){
                    alertBuffering(false);
                }
                reportIncident(im);
                msgStorage.saveMessage(im, IncidentMessage.class.getSimpleName());
            }
        }
    }

    public void bufferShip(PositionMessage ps) throws ShipServiceException, MarshalException, IOException, ValidationException {
        //if ship already exists
        Ship ship;
        if (shipBuffer.exitsShip(ps.getShipId())) {
            ship = shipBuffer.getShip(ps.getShipId());
            shipBuffer.updateShip(ship, ps.getAfstandTotLoskade());
        } else {
            ship = new Ship(ps.getShipId(), ps.getCentraleId(), ps.getTimestamp(), ps.getAfstandTotLoskade());
            ship.setShipInfo(collectShipInfo(ship.getShipId()));
            shipBuffer.addShip(ship);
        }
        if (isShipMoving(ship)) {
            reportShipBigOffense(ship); //bij overtredingen
        }
        positionShipCache.addPosMessage(shipBuffer.getShip(ps.getShipId()), ps); //USE ETA GENERATOR
    }

    public Boolean isShipMoving(Ship ship) {
        boolean distanceChanging = false;
        if (incidentAlert) {
            NavigableMap<Ship, ArrayList<PositionMessage>> shipPositions = new TreeMap<>();
            shipPositions.put(shipBuffer.getShip(ship.getShipId()), positionShipCache.getPosMessages().get(ship));
            distanceChanging = this.etaGenerator.isSpeeding(shipPositions);
        }
        return distanceChanging;
    }

    public ShipInfo collectShipInfo(int shipId) throws ShipServiceException {
        ShipInfo shipInfo = shipService.getShipInfo(shipId);
        shipInfoCache.cacheShipInfo(shipId, shipInfo);
        return shipInfo;
    }

    private void reportIncident(IncidentMessage im) throws MarshalException, IOException, ValidationException {
        IncidentReport report = new IncidentReport();
        if (im != null) {
            Ship ship = shipBuffer.getShip(im.getShipId());
            report.setShip(ship);
            report.setType(im.getType());
            report.setAction(generateAction(im.getType(), ship));
            alertBuffering(true);
        }
        outMessageQueue.send(report);
    }

    private void reportShipBigOffense(Ship ship) throws MarshalException, IOException, ValidationException {
        IncidentReport report = new IncidentReport();
        report.setShip(ship);
        report.setType("");
        report.setAction("ZwareOvertreding");
        outMessageQueue.send(report);
    }

    private void alertBuffering(Boolean alert) {
        this.incidentAlert = alert;
    }

    private String generateAction(String type, Ship ship) {
        String action = "";
        if (type.equals("schade")) {
            action = "AlleSchepenInZoneVoorAnker";
            if (ship.getShipInfo().isDangereousCargo()) {
                action = "AlleSchepenVoorAnker";
            }
        } else if (type.equals("man overboord")) {
            action = "AlleSchepenVoorAnker";
        }
        return action;
    }
}
