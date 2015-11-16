import be.kdg.schelderadar.domain.message.*;
import be.kdg.schelderadar.domain.ProcessingUnit;
import be.kdg.schelderadar.broker.RabbitMQ;
import be.kdg.schelderadar.eta.ETATime;
import be.kdg.schelderadar.eta.ETACaller;
import be.kdg.schelderadar.eta.ETAGenerator;
import be.kdg.schelderadar.out.report.ActionCaller;
import be.kdg.schelderadar.out.report.ActionGenerator;
import be.kdg.schelderadar.out.store.MessageStorage;
import be.kdg.schelderadar.out.store.MessageStorageImpl;
import be.kdg.schelderadar.service.ShipService;
import be.kdg.schelderadar.service.ShipServiceApi;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ProcessigUnitRunner {

    public static void main(String[] args)  {
        MessageStorage msgStorage = new MessageStorageImpl();
        MessageConverter messageConverter = new CastorMessageConverter();
        ETACaller ETACaller = new ETAGenerator();
        ShipMessageCollector shipMessageCollector = new ShipMessageCollector();

        //ACTIONS for incident report
        Map<String, String> actionByTypeMap = new HashMap<>();
        actionByTypeMap.put("Man over boord", "AlleSchepenVoorAnker");
        actionByTypeMap.put("Schade", "AlleSchepenInZoneVoorAnker");
        actionByTypeMap.put("Medisch noodgeval", "AlleSchepenInZoneVoorAnker");
        //if dangerousCargo true -> send always 'AlleSchepenVoorAnker'
        actionByTypeMap.put("dangerous", "AlleSchepenVoorAnker");

        ActionCaller actionCaller = new ActionGenerator(actionByTypeMap);

        ProcessingUnit pu = new ProcessingUnit(shipMessageCollector, msgStorage, actionCaller);
        ShipService shipService = new ShipServiceApi("www.services4se3.com/shipservice/");

        /*SERVER rabbitMQ --> running*/
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
            if(connection!=null){
                channel = connection.createChannel();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //Analyzer in QUEUE --> converting POSITION or INCIDENT
        MessageAnalyzer messageAnalyzer = new ShipMessageAnalyzer(messageConverter, shipMessageCollector);

        //incoming messages : position or incident  (QUEUE_NAME -> localhost queuename)
        pu.setInMessageQueue(new RabbitMQ("SHIPINFO", channel, messageAnalyzer, messageConverter));

        //outgoing messages : report of incident (QUEUE_NAME -> localhost queuename)
        pu.setOutMessageQueue( new RabbitMQ("REPORT", channel, messageAnalyzer, messageConverter));

        /*use ShipInfo Api | proxy */
        pu.setShipService(shipService);

        /* max time ship will be buffered (0 messages are coming from the messageQueue for 50SECONDS)*/
        pu.setTimeToInterrupt(50000);

        /* max time ShipBuffer will be cache ship info */
        pu.setTimeToClearCache(500000);

        //ETA Caller for Eta generator
        pu.setEtaCaller(ETACaller);

        //POSSIBILITIES: ZONE | POSITION \ NORMAL = (pu.setShipIdsForETA() implementation);
        pu.setEtaTime(ETATime.NORMAL);

        //if EtaTime.NORMAL -> sending List with ShipIds
        ArrayList<Integer> shipIds = new ArrayList<>();
        shipIds.add(1234567);
        pu.setShipIdsForETA(shipIds);

        //START PROCESSOR
        if(channel!=null) {
            pu.start();
        }

    }
}
