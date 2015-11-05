import be.kdg.schelderadar.broker.MessageQueue;
import be.kdg.schelderadar.broker.RabbitMQ;
import be.kdg.schelderadar.domain.message.MessageAnalyzer;
import be.kdg.schelderadar.domain.message.ShipMessageAnalyzer;
import be.kdg.schelderadar.domain.ship.Ship;
import be.kdg.schelderadar.domain.ship.ShipInfo;
import be.kdg.schelderadar.out.report.IncidentReport;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * User: michaelkees
 * Date: 05/11/15
 */
public class IncidentReportSender {
    public static void main(String[] args) throws IOException, TimeoutException, MarshalException, ValidationException {
        IncidentReport incidentReport = new IncidentReport();


        Ship testShip = new Ship();
        testShip.setShipId(1234567);
        testShip.setTimestamp(new Date());
        testShip.setCentraleId("Antwerpen");
        testShip.setAfstandTotLoskade(30000);
        testShip.setShipInfo(new ShipInfo("IMO1234567", true,20, null));

        incidentReport.setShip(testShip);
        incidentReport.setAction("AlleSchepenVoorAnker");
        incidentReport.setType("schade");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        MessageQueue messageQueue = new RabbitMQ("REPORT", channel, null);


        messageQueue.send(incidentReport);

    }
}
