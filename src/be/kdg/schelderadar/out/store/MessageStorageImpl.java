package be.kdg.schelderadar.out.store;

import be.kdg.schelderadar.domain.message.IncidentMessage;
import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.eta.ETAReport;
import javafx.geometry.Pos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: michaelkees
 * Data Storage --> messages from generator --> System println
 */
public class MessageStorageImpl implements MessageStorage {
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public MessageStorageImpl() {
    }

    @Override
    public void saveMessage(Object object) {

        System.out.println(((PositionMessage)object).getShipId());

        //TODO CHECK OBJECT for print
        if (object.equals(PositionMessage.class)) {

            PositionMessage ps = (PositionMessage) object;
            System.out.printf("data %s: ship: %d distance: %d \n", df.format(ps.getTimestamp()), ps.getShipId(), ps.getAfstandTotLoskade());

        } else if (object.equals(IncidentMessage.class)) {
            IncidentMessage im = (IncidentMessage) object;
            System.out.printf("incident: ship: %d action: %s", im.getShipId(), im.getAction() );

        } else if (object.equals(ETAReport.class)) {
            ETAReport etAreport = (ETAReport) object;
            System.out.println(etAreport);

        }
    }

}
