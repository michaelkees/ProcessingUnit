package be.kdg.schelderadar.in;


import be.kdg.schelderadar.domain.MessageCollector;
import be.kdg.schelderadar.domain.MessageConverter;

import be.kdg.schelderadar.domain.PositionMessage;
import com.rabbitmq.client.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class RabbitMQReceiver implements RabbitMQ {
    private final String QUEUE_NAME;

    private Consumer consumer;
    private Channel channel;
    private MessageCollector msgCollector;

    public RabbitMQReceiver(String queueName, Channel channel, MessageCollector msgCollector) {
        this.QUEUE_NAME = queueName;
        this.channel = channel;
        this.msgCollector = msgCollector;
    }

    @Override
    public void init() throws RabbitMQException {
        if (consumer == null) {
            try {
                this.consumer = new DefaultConsumer(this.channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                            throws IOException {
                        try {
                            String message = new String(body, "UTF-8");
                            getMessage(message);

                        } catch (MarshalException | ValidationException e) {
                            throw new IOException("Error reading msg");
                        }
                    }
                };
                this.channel.basicConsume(QUEUE_NAME, true, consumer);
            } catch (IOException e) {
                this.consumer = null;
                throw new RabbitMQException(e.getMessage(), e);
            }
        }

    }

    public void getMessage(String message) throws MarshalException, ValidationException {
        MessageConverter converter = new MessageConverter();
        if (message.contains("position")){
            PositionMessage ps = (PositionMessage) converter.convertXMLToJava(message);
            msgCollector.addPostitionMessage(ps);
        }
        //TODO: check if Position message or Incident messagE
    }

}
