import be.kdg.schelderadar.broker.MessageQueue;
import be.kdg.schelderadar.domain.message.*;
import be.kdg.schelderadar.domain.ProcessingUnit;
import be.kdg.schelderadar.cache.ShipInfoCache;
import be.kdg.schelderadar.broker.MQException;
import be.kdg.schelderadar.broker.RabbitMQ;
import be.kdg.schelderadar.out.store.MessageStorage;
import be.kdg.schelderadar.out.store.MessageStorageImpl;
import be.kdg.schelderadar.service.ShipService;
import be.kdg.schelderadar.service.ShipServiceApi;
import be.kdg.schelderadar.service.ShipServiceException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ProcessigUnitTest {

    public static void main(String[] args) throws MQException, ShipServiceException, IOException, TimeoutException, MarshalException, ValidationException {
        MessageStorage msgStorage = new MessageStorageImpl();
        MessageConverter messageConverter = new CastorMessageConverter();
        ShipMessageCollector shipMessageCollector = new ShipMessageCollector();
        ShipInfoCache shipInfoCache = new ShipInfoCache(50000);

        ProcessingUnit pu = new ProcessingUnit(shipMessageCollector, msgStorage);
        ShipService shipService = new ShipServiceApi("www.services4se3.com/shipservice/");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        MessageAnalyzer messageAnalyzer = new ShipMessageAnalyzer(messageConverter, shipMessageCollector);
        MessageQueue inMessageQueue = new RabbitMQ("SHIPINFO", channel, messageAnalyzer);
        MessageQueue outMessageQueue = new RabbitMQ("REPORT", channel, messageAnalyzer);
        pu.setInMessageQueue(inMessageQueue);
        pu.setOutMessageQueue(outMessageQueue);
        pu.setShipService(shipService);
        pu.setTimeToInterrupt(50000);
        pu.setShipInfoCache(shipInfoCache);
        pu.start();

    }
}
