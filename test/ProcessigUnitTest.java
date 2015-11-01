import be.kdg.schelderadar.domain.MessageCollector;
import be.kdg.schelderadar.domain.ProcessingUnit;
import be.kdg.schelderadar.domain.ShipCache;
import be.kdg.schelderadar.in.RabbitMQException;
import be.kdg.schelderadar.in.RabbitMQReceiver;
import be.kdg.schelderadar.out.MessageStorage;
import be.kdg.schelderadar.out.ShipMessageStorage;
import be.kdg.schelderadar.service.ShipService;
import be.kdg.schelderadar.service.ShipServiceApi;
import be.kdg.schelderadar.service.ShipServiceException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ProcessigUnitTest {
    private static final String QUEUE_NAME = "SHIPINFO";

    public static void main(String[] args) throws RabbitMQException, ShipServiceException, IOException, TimeoutException {
        MessageStorage msgStorage = new ShipMessageStorage();
        MessageCollector msgCollector = new MessageCollector(msgStorage);
        ShipCache shipCache = new ShipCache(50000);


        ProcessingUnit pu = new ProcessingUnit(msgCollector);
        ShipService shipService = new ShipServiceApi("www.services4se3.com/shipservice/");


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        pu.setRabbitMQ(new RabbitMQReceiver(QUEUE_NAME,channel,msgCollector ));
        pu.setShipService(shipService);
        pu.setTimeToInterrupt(50000);
        pu.setShipCache(shipCache);
        pu.start();

    }
}
