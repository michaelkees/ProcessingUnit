package be.kdg.schelderadar.domain;

import be.kdg.schelderadar.broker.MessageQueue;
import be.kdg.schelderadar.broker.MQException;
import be.kdg.schelderadar.cache.ShipMessageMapper;

import be.kdg.schelderadar.domain.message.IncidentMessage;
import be.kdg.schelderadar.domain.message.ShipMessageCollector;
import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.domain.ship.Ship;
import be.kdg.schelderadar.domain.ship.ShipInfo;

import be.kdg.schelderadar.eta.ETAReport;
import be.kdg.schelderadar.eta.ETATime;
import be.kdg.schelderadar.eta.ETACaller;
import be.kdg.schelderadar.out.report.ActionCaller;
import be.kdg.schelderadar.out.report.ActionCallerException;
import be.kdg.schelderadar.out.report.IncidentReport;
import be.kdg.schelderadar.out.store.MessageStorage;
import be.kdg.schelderadar.service.ShipService;
import be.kdg.schelderadar.service.ShipServiceException;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;
import java.util.*;

/**
 * Controller class
 */

public class ProcessingUnit {
    private int timeToInterrupt = 100000; //DEFAULT
    private int timeToClearCache = 500000; //DEFAULT
    private String currentActionProcessingBuffer;
    private String currentIncidentZone;
    private MessageQueue inMessageQueue;
    private MessageQueue outMessageQueue;
    private final static Logger logger = Logger.getLogger(ProcessingUnit.class);

    private ShipService shipService;
    private boolean isReceiving;
    private boolean incidentAlert;

    private ShipMessageCollector msgCollector;
    private ETATime etaTime;
    private ETACaller etaCaller;
    private MessageStorage msgStorage;

    //collectors/cache classes
    private final ShipBuffer shipBuffer;

    private ActionCaller actionCaller;
    private ArrayList<Integer> shipIdsForETA = new ArrayList<>();

    public ProcessingUnit(ShipMessageCollector msgCollector, MessageStorage storage, ActionCaller actionCaller) {
        this.shipBuffer = new ShipBuffer(timeToInterrupt, new ShipMessageMapper(timeToClearCache));
        this.msgCollector = msgCollector;
        this.actionCaller = actionCaller;
        this.msgStorage = storage;

        this.isReceiving = false;
        this.incidentAlert = false;
    }

    public void setShipIdsForETA(ArrayList<Integer> shipIdsForETA) {
        this.shipIdsForETA = shipIdsForETA;
    }

    public void start() {
        isReceiving = true;
        while (isReceiving) {
            try {
                inMessageQueue.init();
            } catch (MQException e) {
                logger.error(e.getMessage());
            }
            try {
                performCollect();
            } catch (ShipServiceException | ActionCallerException e) {
                logger.error(e.getMessage());
            }
            checkBufferedShipSignal();
            checkCacheClear();
            checkETAgeneration();
            msgCollector.clear();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Error during sleeping thread (1000)");
            }
        }
    }

    public void stop() {
        isReceiving = true;
    }

    private void checkETAgeneration() {
        ArrayList<Integer> shipIds = new ArrayList<>();
        if(etaTime!=null) {
            switch (etaTime) {
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
                            if (shipBuffer.getShipMessageMapper().getShipPositionMessages().get(shipBuffer.getShip(ps.getShipId())).size() > 1) {
                                ArrayList<PositionMessage> shipMessages = shipBuffer.getShipMessageMapper().getShipPositionMessages().get(shipBuffer.getShip(ps.getShipId()));
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
        } else {
            logger.warn("ETATime not created/set");
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
                shipMap.put(s, shipBuffer.getShipMessageMapper().getShipPositionMessages().get(s));
            }
        }
        if (!shipMap.isEmpty()) {
            Map<Ship, Long> shipETAs = etaCaller.calculateEstimatedTimeOfArrivalsMap(shipMap);
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
        shipBuffer.getShipMessageMapper().checkCacheClear();
    }

    private void checkBufferedShipSignal() {
        shipBuffer.checkSignalShip();
    }

    public void performCollect() throws ShipServiceException, ActionCallerException {
        if (!msgCollector.getPositionMessages().isEmpty()) {
            for (PositionMessage ps : msgCollector.getPositionMessages()) {
                try {
                    bufferShip(ps);
                } catch (MarshalException | IOException | ValidationException e) {
                    logger.error("Error during buffering ship");
                    throw new ShipServiceException(e.getMessage(), e);
                }
                msgStorage.saveMessage(ps, PositionMessage.class.getSimpleName());
            }
        }
        if (!msgCollector.getIncidentMessages().isEmpty()) {
            for (IncidentMessage im : msgCollector.getIncidentMessages()) {
                if (im.getType().equals("alles normaal")) {
                    alertBuffering(false);
                    currentActionProcessingBuffer="";
                    currentIncidentZone="";
                }
                try {
                    reportIncident(im);
                } catch (MarshalException | IOException | ValidationException e) {
                    logger.error("Error during reporting incident message");
                    throw new ShipServiceException(e.getMessage(), e);
                } catch (ActionCallerException e) {
                    logger.error("Error during getting action incident");
                    throw new ActionCallerException(e.getMessage(), e);

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

        shipBuffer.getShipMessageMapper().addPosMessage(ship, ps);

        if (incidentAlert) {

            if (isShipMoving(ship)) {
                reportShipBigOffense(ship); //bij overtredingen
            }
        }
    }

    public Boolean isShipMoving(Ship ship) {
        if(currentActionProcessingBuffer.contains("Zone") && ship.getCentraleId().equals(currentIncidentZone)){
            NavigableMap<Ship, ArrayList<PositionMessage>> shipPositions = new TreeMap<>();
            shipPositions.put(shipBuffer.getShip(ship.getShipId()), shipBuffer.getShipMessageMapper().getShipPositionMessages().get(ship));
            return this.etaCaller.analyzeSpeedShip(shipPositions);
        } else if(!currentActionProcessingBuffer.contains("Zone")){
            NavigableMap<Ship, ArrayList<PositionMessage>> shipPositions = new TreeMap<>();
            shipPositions.put(shipBuffer.getShip(ship.getShipId()), shipBuffer.getShipMessageMapper().getShipPositionMessages().get(ship));
            return this.etaCaller.analyzeSpeedShip(shipPositions);
        }
        return false;
    }

    public ShipInfo collectShipInfo(int shipId) throws ShipServiceException {
        return shipService.getShipInfo(shipId);
    }

    private void reportIncident(IncidentMessage im) throws MarshalException, IOException, ValidationException, ActionCallerException {
        if (!incidentAlert) {
            IncidentReport report = new IncidentReport();

            if (im != null) {
                Ship ship = shipBuffer.getShip(im.getShipId());
                report.setShip(ship);
                report.setType(im.getType());
                report.setAction(generateAction(im.getType(), ship.getShipInfo().isDangereousCargo()));
                alertBuffering(true);

                currentActionProcessingBuffer = report.getAction();
                currentIncidentZone = shipBuffer.getShip(im.getShipId()).getCentraleId();
            }

            outMessageQueue.send(report);
        }
    }

    private void reportShipBigOffense(Ship ship) throws MarshalException, IOException, ValidationException {
        IncidentReport report = new IncidentReport();
        report.setShip(ship);
        report.setType("");
        report.setAction("ZwareOvertreding");
        currentActionProcessingBuffer = report.getAction();
        outMessageQueue.send(report);
    }

    private void alertBuffering(Boolean alert) {
        this.incidentAlert = alert;
    }

    private String generateAction(String type, Boolean dangerousCargo) throws ActionCallerException {
        if(actionCaller!=null){
            return actionCaller.getAction(type, dangerousCargo);
        } else {
            throw new ActionCallerException("ActionCaller never created.", new Throwable("ACTIONCALLER CREATION ERROR"));
        }
    }


    //adjustable setters for processingUnit
    public void setEtaTime(ETATime etaTime) {
        this.etaTime = etaTime;
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

    public void setEtaCaller(ETACaller etaCaller) {
        this.etaCaller = etaCaller;
    }

    public void setActionCaller(ActionCaller actionCaller) {
        this.actionCaller = actionCaller;
    }

    public void setTimeToClearCache(int timeToClearCache) {
        this.timeToClearCache = timeToClearCache;
    }
}
