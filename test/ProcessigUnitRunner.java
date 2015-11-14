import be.kdg.schelderadar.domain.ProcessingException;
import be.kdg.schelderadar.domain.message.*;
import be.kdg.schelderadar.domain.ProcessingUnit;
import be.kdg.schelderadar.cache.ShipInfoCache;
import be.kdg.schelderadar.broker.MQException;
import be.kdg.schelderadar.broker.RabbitMQ;
import be.kdg.schelderadar.eta.ETATime;
import be.kdg.schelderadar.eta.ETACaller;
import be.kdg.schelderadar.eta.ETAGenerator;
import be.kdg.schelderadar.out.report.ActionCaller;
import be.kdg.schelderadar.out.report.ActionCallerException;
import be.kdg.schelderadar.out.report.ActionGenerator;
import be.kdg.schelderadar.out.store.MessageStorage;
import be.kdg.schelderadar.out.store.MessageStorageImpl;
import be.kdg.schelderadar.service.ShipService;
import be.kdg.schelderadar.service.ShipServiceApi;
import be.kdg.schelderadar.service.ShipServiceException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ProcessigUnitRunner {

    public static void main(String[] args)  {
        MessageStorage msgStorage = new MessageStorageImpl();
        MessageConverter messageConverter = new CastorMessageConverter();

        //AANMAKEN etaGenerator
        ETACaller ETACaller = new ETAGenerator();

        //STOCKAGE VAN RABBITMQ ANALYZER
        ShipMessageCollector shipMessageCollector = new ShipMessageCollector();


        //ACTIONS FOR INCIDENT
        Map<String, String> actionByTypeMap = new HashMap<>();
        actionByTypeMap.put("man overboord", "AlleSchepenVoorAnker");
        actionByTypeMap.put("schade", "AlleSchepenInZoneVoorAnker");
        actionByTypeMap.put("dangerous", "AlleSchepenVoorAnker"); //DEFAULT WAARDE -->  als er dangerous cargo is voor een type incident
        ActionCaller actionCaller = new ActionGenerator(actionByTypeMap);

        /*TIJD CACHE CLEAR = 50 SECONDEN*/
        ShipInfoCache shipInfoCache = new ShipInfoCache(50000);

        ProcessingUnit pu = new ProcessingUnit(shipMessageCollector, msgStorage, actionCaller);

        ShipService shipService = new ShipServiceApi("www.services4se3.com/shipservice/");

        /*SERVER VAN RABBITMQ MOET RUNNEN*/
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            System.out.println(e.getMessage());
        }
        Channel channel = null;
        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //Analyzer in QUEUE --> converteren POSITION OF INCIDENT
        MessageAnalyzer messageAnalyzer = new ShipMessageAnalyzer(messageConverter, shipMessageCollector);

        //voor inkomende messages : position en incident  (QUEUE_NAME -> localhost queuename)
        pu.setInMessageQueue(new RabbitMQ("SHIPINFO", channel, messageAnalyzer));

        //uitgaande messages : report van incident (QUEUE_NAME -> localhost queuename)
        pu.setOutMessageQueue( new RabbitMQ("REPORT", channel, messageAnalyzer));

        /*voor aanspreken van de ShipInfo Api | proxy */
        pu.setShipService(shipService);

        /*tijd dat er maximaal gekeken wordt of het Ship nog in buffer veranderd --> anders wordt buffering van ship onderbroken
                              50 SECONDEN */
        pu.setTimeToInterrupt(50000);


        //MOGELIJK CACHE VOOR SHIPINFO --> (OPDRACHT)
        pu.setShipInfoCache(shipInfoCache);

        //Voor eta calculaties --> instelbaar voor meer verfijnde etagenerator
        pu.setEtaCaller(ETACaller);

        //MOGELIJKHEDEN: ZONE | POSITION \ NORMAL=opvragen (pu.setShipIdsForETA() implementeren);
        pu.setEtaTime(ETATime.NORMAL);

        ArrayList<Integer> shipIds = new ArrayList<>();
        shipIds.add(1234567);
        pu.setShipIdsForETA(shipIds);


        //starten van processing unit voor test

        try {
            pu.start();
        } catch (MQException | ShipServiceException | ProcessingException | ActionCallerException e) {
            System.out.println(e.getMessage());
        }
    }
}
