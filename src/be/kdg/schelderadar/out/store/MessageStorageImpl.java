package be.kdg.schelderadar.out.store;

import be.kdg.schelderadar.domain.message.IncidentMessage;
import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.eta.ETAReport;

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
    public void saveMessage(Object object, String classType) {
        //TODO check object get class
        if (classType.equals(PositionMessage.class.getSimpleName())) {
            PositionMessage ps = (PositionMessage) object;
            System.out.printf("data %s - ship: %d distance: %d zone: %s\n", df.format(ps.getTimestamp()), ps.getShipId(), ps.getAfstandTotLoskade(), ps.getCentraleId());

        } else if (classType.equals(IncidentMessage.class.getSimpleName())) {
            IncidentMessage im = (IncidentMessage) object;
            System.out.printf("incident: ship: %d type: %s \n", im.getShipId(), im.getType() );

        } else if (classType.equals(ETAReport.class.getSimpleName())) {
            ETAReport etAreport = (ETAReport) object;
            System.out.println(etAreport);

        }
    }

}
