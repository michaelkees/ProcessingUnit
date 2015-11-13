package parts;

import be.kdg.schelderadar.domain.message.IncidentMessage;
import be.kdg.schelderadar.domain.message.PositionMessage;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

/**
 * User: michaelkees
 * Date: 01/11/15
 */
public class sendTester {
    public static void main(String[] args) throws IOException, MarshalException, ValidationException {
        StringWriter writer = new StringWriter();
        //Marshaller.marshal(message, writer);

        PositionMessage message = new PositionMessage();
        IncidentMessage incidentMessage = new IncidentMessage();

        message.setShipId(1234567);
        message.setTimestamp(new Date());
        message.setCentraleId("Vlissingen");
        message.setAfstandTotLoskade(4350);

        incidentMessage.setShipId(1234567);
        incidentMessage.setTimestamp(new Date());
        incidentMessage.setType("schade");

        Marshaller marshaller = new Marshaller(writer);

        marshaller.marshal(incidentMessage);
        System.out.println(writer.toString());
    }
}
