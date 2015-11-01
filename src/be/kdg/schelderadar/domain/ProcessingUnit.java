package be.kdg.schelderadar.domain;

import be.kdg.schelderadar.in.RabbitMQ;
import be.kdg.schelderadar.in.RabbitMQException;
import be.kdg.schelderadar.out.MessageStorage;
import be.kdg.schelderadar.service.ShipService;
import be.kdg.schelderadar.service.ShipServiceException;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Controller class
 */

public class ProcessingUnit {
    private int timeToInterrupt = 100000; //DEFAULT
    private RabbitMQ rabbitMQ;
    private ShipService shipService;
    private boolean isReceiving;
    private MessageCollector msgCollector;
    private final ShipBuffer shipBuffer;
    private  ShipCache shipCache;

    public ProcessingUnit(MessageCollector msgCollector) {
        this.shipBuffer = new ShipBuffer(timeToInterrupt);
        this.isReceiving = false;
        this.msgCollector = msgCollector;

    }

    public void setShipCache(ShipCache shipCache) {
        this.shipCache = shipCache;
    }

    public void setTimeToInterrupt(int timeToInterrupt) {
        this.timeToInterrupt = timeToInterrupt;
    }

    public void setRabbitMQ(RabbitMQ rabbitMQ) {
        this.rabbitMQ = rabbitMQ;
    }

    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    public void start() throws RabbitMQException, ShipServiceException {
        isReceiving = true;

        while (isReceiving) {
                rabbitMQ.init();
                performCollect();
                checkBufferedShipSignal();
                checkCacheClear();
                if(!getInfoShipBuffer().isEmpty()){
                    System.out.println(getInfoShipBuffer());
                }
                msgCollector.clear();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkCacheClear() {
        shipCache.checkCacheClear();
    }

    private void checkBufferedShipSignal() {
        shipBuffer.checkSignalShip();
    }

    public void performCollect() throws ShipServiceException {
        for (PositionMessage ps : msgCollector.getPositionMessages()) {
            bufferShip(ps);
        }
    }

    public void bufferShip(PositionMessage ps) throws ShipServiceException {
        //if ship already exists
        if(shipBuffer.exitsShip(ps.getShipId())){
            System.out.println("update ship");
            Ship shipOnRadar = shipBuffer.getShip(ps.getShipId());
            shipBuffer.updateShip(shipOnRadar, ps.getAfstandTotLoskade());

        } else {
            System.out.println("create ship");
            Ship ship = new Ship(ps.getShipId(),ps.getCentraleId(),ps.getTimestamp(),ps.getAfstandTotLoskade());
            ship.setShipInfo(collectShipInfo(ship.getShipId()));
            shipBuffer.addShip(ship);
        }
    }

    public ShipInfo collectShipInfo(int shipId) throws ShipServiceException {
        //cache info
        ShipInfo shipInfo = shipService.getShipInfo(shipId);
        shipCache.cacheShipInfo(shipId, shipInfo);
        return shipInfo;
    }

    public String getInfoShipBuffer(){
        return shipBuffer.toString();
    }

}
