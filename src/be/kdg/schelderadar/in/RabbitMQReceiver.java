package be.kdg.schelderadar.in;


import be.kdg.schelderadar.domain.BrokerException;
import be.kdg.schelderadar.domain.MessageConverter;
import be.kdg.schelderadar.domain.PositionMessage;
import be.kdg.se3.proxy.ShipServiceProxy;
import com.rabbitmq.client.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class RabbitMQReceiver {
    private final static String QUEUE_NAME = "SHIP";
    private final static ShipServiceProxy proxy = new ShipServiceProxy();
    private PositionMessage positionMessage;

    public RabbitMQReceiver() {
        //empty
    }

    public void initialize () throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                try {
                    MessageConverter converter = new MessageConverter();
                    System.out.println(message);

                    //TODO: check if Position message or Incident message
                    positionMessage =  (PositionMessage) converter.convertJavaXML(message, PositionMessage.class);

                } catch (MarshalException e) {
                    //throw new BrokerException(e.getMessage(), e);
                } catch (ValidationException e) {
                    //throw new BrokerException(e.getMessage(), e);
                }

            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

}
