package be.kdg.schelderadar.domain;

import be.kdg.schelderadar.in.RabbitMQReceiver;
import be.kdg.schelderadar.in.ShipServiceApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

/**
 * Controller class
 */

public class ProcessingUnit {
    RabbitMQReceiver rabbitMQ;
    ShipServiceApi shipService;
    boolean isReceiving;

    private final ShipBuffer shipBuffer;

    public ProcessingUnit(RabbitMQReceiver rabbitMQ, ShipServiceApi shipServiceApi) {
        this.rabbitMQ = rabbitMQ;
        this.shipService = shipServiceApi;
        shipBuffer = new ShipBuffer();
    }

    public void setRabbitMQ(RabbitMQReceiver rabbitMQ) {
        this.rabbitMQ = rabbitMQ;
    }

    public void setShipService(ShipServiceApi shipService) {
        this.shipService = shipService;
    }

    public void start(){
        isReceiving = true;
        /*try {
            //this.rabbitMQ.init();
            while(isReceiving) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } */
        //TEST: www.services4se3.com/shipservice/shipid
    }
}
