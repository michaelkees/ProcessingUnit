package be.kdg.schelderadar.broker;


import be.kdg.schelderadar.domain.message.*;

import be.kdg.schelderadar.out.report.IncidentReport;
import com.rabbitmq.client.*;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeoutException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class RabbitMQ implements MessageQueue {
    private final String QUEUE_NAME;
    private final static Logger logger = Logger.getLogger(RabbitMQ.class);

    private Consumer consumer;
    private Channel channel;
    private MessageAnalyzer msgAnalyzer;
    private MessageConverter msgConverter;

    public RabbitMQ(String queueName, Channel channel, MessageAnalyzer msgAnalyzer, MessageConverter msgConverter) {
        this.QUEUE_NAME = queueName;
        this.channel = channel;
        this.msgAnalyzer = msgAnalyzer;
        this.msgConverter = msgConverter;
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
                            logger.error("Unexpected error during converting message (Marshaller)");
                            throw new IOException("Error converting messages", e.getCause());
                        }
                    }
                };
                this.channel.basicConsume(QUEUE_NAME, true, consumer);
            } catch (IOException e) {
                this.consumer = null;
                logger.error("Unexpected error during connecting to Message Queue (SERVER)");
                throw new MQException(e.getMessage(), e);
            }
        }

    }

    @Override
    public void send(IncidentReport iReport) throws IOException, MarshalException, ValidationException {
        channel.basicPublish("", QUEUE_NAME, null, msgConverter.convertJavaToXML(iReport).getBytes());
    }

    @Override
    public void shutdown() throws IOException, TimeoutException {
        this.channel.close();
    }

    public void setMsgAnalyzer(MessageAnalyzer msgAnalyzer) {
        this.msgAnalyzer = msgAnalyzer;
    }
}
