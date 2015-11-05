import be.kdg.schelderadar.broker.MQException;
import be.kdg.schelderadar.broker.MessageQueue;
import be.kdg.schelderadar.broker.RabbitMQ;
import be.kdg.schelderadar.domain.message.*;
import be.kdg.schelderadar.out.store.MessageStorage;
import be.kdg.schelderadar.out.store.MessageStorageImpl;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * User: michaelkees
 * Date: 05/11/15
 */
public class RabbitMQReceiver {
    public static void main(String[] args) throws IOException, TimeoutException, MQException {

        boolean isReceiving = false;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        MessageStorage storage = new MessageStorageImpl();

        MessageConverter messageConverter = new CastorMessageConverter();
        ShipMessageCollector collector = new ShipMessageCollector();
        MessageAnalyzer messageAnalyzer = new ShipMessageAnalyzer(messageConverter, collector);


        MessageQueue inMessageQueue = new RabbitMQ("SHIPINFO", channel, messageAnalyzer);

        isReceiving = true;
        while (isReceiving) {
            inMessageQueue.init();
            if (!collector.getPositionMessages().isEmpty()) {
                for (PositionMessage p : collector.getPositionMessages()) {
                    storage.saveMessage(p, PositionMessage.class.getSimpleName());
                }
            }

            if (!collector.getIncidentMessages().isEmpty()) {
                for (IncidentMessage i : collector.getIncidentMessages()) {
                     storage.saveMessage(i, IncidentMessage.class.getSimpleName());
                }
            }
            collector.clear();
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
