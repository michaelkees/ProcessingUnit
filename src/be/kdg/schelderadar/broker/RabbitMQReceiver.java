package be.kdg.schelderadar.broker;


import be.kdg.schelderadar.domain.message.*;

import com.rabbitmq.client.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class RabbitMQReceiver implements MessageQueue {
    private final String QUEUE_NAME;

    private Consumer consumer;
    private Channel channel;
    private MessageAnalyzer msgAnalyzer;

    public RabbitMQReceiver(String queueName, Channel channel, MessageAnalyzer msgAnalyzer) {
        this.QUEUE_NAME = queueName;
        this.channel = channel;
        this.msgAnalyzer = msgAnalyzer;
    }

    @Override
    public void init() throws MQException {
        if (consumer == null) {
            try {
                this.consumer = new DefaultConsumer(this.channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                            throws IOException {
                        try {
                            String message = new String(body, "UTF-8");

                            msgAnalyzer.analyzeMessage(message);

                        } catch (MarshalException | ValidationException e) {
                            throw new IOException("Error reading msg");
                        }
                    }
                };
                this.channel.basicConsume(QUEUE_NAME, true, consumer);
            } catch (IOException e) {
                this.consumer = null;
                throw new MQException(e.getMessage(), e);
            }
        }

    }

    public void setMsgAnalyzer(MessageAnalyzer msgAnalyzer) {
        this.msgAnalyzer = msgAnalyzer;
    }
}
