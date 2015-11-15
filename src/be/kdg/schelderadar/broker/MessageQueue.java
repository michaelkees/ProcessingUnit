package be.kdg.schelderadar.broker;

import be.kdg.schelderadar.out.report.IncidentReport;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public interface MessageQueue {
    /**
     * initializing the consumer and handle incoming messages from the message queue
     * @throws MQException
     */
    void init() throws MQException;

    /**
     * sending xml message to the message queue, especially incident reports
     * @param incidentReport
     * @throws IOException
     * @throws MarshalException
     * @throws ValidationException
     */
    void send(IncidentReport incidentReport) throws IOException, MarshalException, ValidationException;

    /**
     * shutdown the message queue
     * @throws IOException
     * @throws TimeoutException
     */
    void shutdown() throws IOException, TimeoutException;

}
