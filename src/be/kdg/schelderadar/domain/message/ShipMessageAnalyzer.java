package be.kdg.schelderadar.domain.message;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

/**
 * User: michaelkees
 * Date: 01/11/15
 */
public class ShipMessageAnalyzer implements MessageAnalyzer {
    ShipMessageCollector shipMessageCollector;
    MessageConverter converter;

    public ShipMessageAnalyzer(MessageConverter converter, ShipMessageCollector shipMessageCollector) {
        this.converter = converter;
        this.shipMessageCollector = shipMessageCollector;
    }

    @Override
    public void analyzeMessage(String message) throws MarshalException, ValidationException {
        if (message.contains("position")) {
            PositionMessage ps = (PositionMessage) converter.convertXMLToJava(message, PositionMessage.class);
            shipMessageCollector.addPostitionMessage(ps);

        } else if (message.contains("incident")) {
            IncidentMessage im = (IncidentMessage) converter.convertXMLToJava(message, IncidentMessage.class);
            shipMessageCollector.addIncidentMessage(im);

        }

    }
}
