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
        message.setShipId(1234567);
        message.setCentraleId("Vlissingen");
        message.setAfstandTotLoskade(4350);
        message.setTimestamp(new Date());

        Marshaller marshaller = new Marshaller(writer);
        marshaller.marshal(message);
        System.out.println(writer.toString());
    }
}
