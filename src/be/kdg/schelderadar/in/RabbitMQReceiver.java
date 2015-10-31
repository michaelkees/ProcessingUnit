package be.kdg.schelderadar.in;


import be.kdg.schelderadar.domain.BrokerException;
import be.kdg.schelderadar.domain.MessageAnalyzer;
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
    private final String QUEUE_NAME;

    private Channel channel;
    private Consumer consumer;
    private MessageAnalyzer msgAnalyzer;

    public RabbitMQReceiver(String QUEUE_NAME, Channel channel, MessageAnalyzer msgAnalyzer) {
        this.QUEUE_NAME = QUEUE_NAME;
        this.channel = channel;
        this.msgAnalyzer = msgAnalyzer;
    }

    public void init() throws RabbitMQException {
        if (consumer != null) {
            try {
                this.consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                            throws IOException {

                        String message = new String(body, "UTF-8");
                        MessageConverter converter = new MessageConverter();

                        //TODO: check if Position message or Incident message
                        try {

                            PositionMessage ps = (PositionMessage) converter.convertJavaXML(message, PositionMessage.class);
                            msgAnalyzer.analyzeMessage(ps);

                        } catch (MarshalException | ValidationException e ){
                            throw new IOException("Error reading msg");
                        }
                    }
                };
                channel.basicConsume(QUEUE_NAME, true, consumer);
            } catch (IOException e) {
                this.consumer = null;
                throw new RabbitMQException(e.getMessage(), e);
            }
        }

    }

    public void setMsgAnalyzer(MessageAnalyzer msgAnalyzer) {
        this.msgAnalyzer = msgAnalyzer;
    }
}
