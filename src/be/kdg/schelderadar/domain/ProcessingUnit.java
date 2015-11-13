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
import org.omg.CORBA.portable.ApplicationException;

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

    private ETAgenerator etaGenerator;  //OUTDATE

    private MessageStorage msgStorage;

    //collectors/cache classes
    private final ShipBuffer shipBuffer;
    private ShipInfoCache shipInfoCache;
    private PositionShipCache positionShipCache;

    //ETA GENERATION
    private ArrayList<Integer> shipIdsForETA = new ArrayList<>();

    public ProcessingUnit(ShipMessageCollector msgCollector, MessageStorage storage) {
        this.shipBuffer = new ShipBuffer(timeToInterrupt);
        this.isReceiving = false;
        this.msgCollector = msgCollector;

        this.etaGenerator = new ETAgenerator(ETATime.NORMAL); //DEFAULT
        this.positionShipCache = new PositionShipCache();
        this.msgStorage = storage;
        this.incidentAlert = false;
    }

    public void setShipIdsForETA(ArrayList<Integer> shipIdsForETA) {
        this.shipIdsForETA = shipIdsForETA;
    }

    public void start() throws MQException, ShipServiceException {
        isReceiving = true;
        while (isReceiving) {
            inMessageQueue.init();
            performCollect();
            checkBufferedShipSignal();
            checkCacheClear();
            checkETAgeneration();
            msgCollector.clear();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //throw new ApplicationException()
            }
        }
    }

    public void stop() {
        isReceiving = true;
    }

    private void checkETAgeneration() {
        ArrayList<Integer> shipIds = new ArrayList<>();
        switch (etaGenerator.getEtaTime()) {
            case NORMAL:
                if (!shipIdsForETA.isEmpty() && !msgCollector.getPositionMessages().isEmpty()) {
                    if (!shipBuffer.getShipList().isEmpty()) {
                        shipIds.addAll(shipIdsForETA);
                    }
                }
                break;
            case POSITION:
                if (!msgCollector.getPositionMessages().isEmpty()) {
                    for (PositionMessage ps : msgCollector.getPositionMessages()) {
                        shipIds.add(ps.getShipId());
                    }
                }
                break;
            case ZONE:
                if (!msgCollector.getPositionMessages().isEmpty()) {

                    for (PositionMessage ps : msgCollector.getPositionMessages()) {
                        if (positionShipCache.getPosMessages().get(shipBuffer.getShip(ps.getShipId())).size() > 1) {
                            ArrayList<PositionMessage> shipMessages = positionShipCache.getPosMessages().get(shipBuffer.getShip(ps.getShipId()));
                            String prevZone = shipMessages.get(shipMessages.size() - 2).getCentraleId();
                            String currZone = shipMessages.get(shipMessages.size() - 1).getCentraleId();
                            if (!prevZone.equals(currZone)) {
                                shipIds.add(ps.getShipId());
                            }
                        }
                    }
                }
        }
        if (!shipIds.isEmpty()) {
            doETAgeneration(shipIds);
        }

    }

    private void doETAgeneration(ArrayList<Integer> shipIdsForETA) {
        if (!shipIdsForETA.isEmpty() && reportETAs(shipIdsForETA) != null) {
            msgStorage.saveMessage(reportETAs(shipIdsForETA), ETAReport.class.getSimpleName());
        }
    }

    public ETAReport reportETAs(ArrayList<Integer> shipIds) {
        ETAReport report = new ETAReport();
        Map<Ship, ArrayList<PositionMessage>> shipMap = new TreeMap<>();
        for (Integer shipId : shipIds) {
            if (shipBuffer.existsShip(shipId)) {
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

    public void performCollect() throws ShipServiceException {
        if (!msgCollector.getPositionMessages().isEmpty()) {
            for (PositionMessage ps : msgCollector.getPositionMessages()) {
                try {
                    bufferShip(ps);
                } catch (MarshalException | IOException | ValidationException e) {
                    throw new ShipServiceException(e.getMessage(), e);
                }
                msgStorage.saveMessage(ps, PositionMessage.class.getSimpleName());
            }
        }
        if (!msgCollector.getIncidentMessages().isEmpty()) {
            for (IncidentMessage im : msgCollector.getIncidentMessages()) {
                if (im.getType().equals("alles normaal")) {
                    alertBuffering(false);
                }
                try {
                    reportIncident(im);
                } catch (MarshalException | IOException | ValidationException e) {
                    throw new ShipServiceException(e.getMessage(), e);
                }
                msgStorage.saveMessage(im, IncidentMessage.class.getSimpleName());
            }
        }
    }

    public void bufferShip(PositionMessage ps) throws ShipServiceException, MarshalException, IOException, ValidationException {
        //if ship already exists
        Ship ship;
        if (shipBuffer.existsShip(ps.getShipId())) {
            ship = shipBuffer.getShip(ps.getShipId());
            shipBuffer.updateShip(ship, ps.getAfstandTotLoskade());
        } else {
            ship = new Ship(ps.getShipId(), ps.getCentraleId(), ps.getTimestamp(), ps.getAfstandTotLoskade());
            ship.setShipInfo(collectShipInfo(ship.getShipId()));
            shipBuffer.addShip(ship);
        }
        //CHECK if alertBuffering
        if (incidentAlert) {
            if (isShipMoving(ship)) {
                reportShipBigOffense(ship); //bij overtredingen
            }
        }
        positionShipCache.addPosMessage(ship, ps);
    }

    public Boolean isShipMoving(Ship ship) {
        NavigableMap<Ship, ArrayList<PositionMessage>> shipPositions = new TreeMap<>();
        shipPositions.put(shipBuffer.getShip(ship.getShipId()), positionShipCache.getPosMessages().get(ship));
        return this.etaGenerator.analyzeSpeedShip(shipPositions);
    }

    public ShipInfo collectShipInfo(int shipId) throws ShipServiceException {
        ShipInfo shipInfo = shipService.getShipInfo(shipId);
        shipInfoCache.cacheShipInfo(shipId, shipInfo);
        return shipInfo;
    }

    private void reportIncident(IncidentMessage im) throws MarshalException, IOException, ValidationException {
        if (!incidentAlert) {
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

    //ACTION GENERATOR KLASSE
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
    ////

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

}
