import be.kdg.schelderadar.broker.RabbitMQException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class RabbitMqTest {
    private static final String QUEUE_NAME = "SHIPINFO";
    public static void main(String[] args) throws IOException, TimeoutException, RabbitMQException {

        /*MessageCollector msgCollector = new MessageCollector();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        System.out.println(channel.toString());

//        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("[Receiver] Waiting for messages...");

        RabbitMQ rabbitMQ = new RabbitMQReceiver("SHIPINFO", channel, msgCollector ) ;

        rabbitMQ.init();  */

    }
}
