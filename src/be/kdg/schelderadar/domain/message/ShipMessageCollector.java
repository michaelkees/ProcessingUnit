package be.kdg.schelderadar.domain.message;

import be.kdg.schelderadar.out.store.MessageStorage;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: michaelkees
 * Date: 04/11/15
 */
public class ShipMessageCollector {
    private Collection<PositionMessage> positionMessages = new ArrayList<>();
    private Collection<IncidentMessage> incidentMessages = new ArrayList<>();

    public ShipMessageCollector() {
    }

    public Collection<PositionMessage> getPositionMessages() {
        return positionMessages;
    }

    public void addPostitionMessage(PositionMessage message) {
        this.positionMessages.add(message);
    }

    public Collection<IncidentMessage> getIncidentMessages() {
        return incidentMessages;
    }

    public void addIncidentMessage(IncidentMessage incidentMessage) {
        this.incidentMessages.add(incidentMessage);
    }

    public void clear(){
        positionMessages.clear();
        incidentMessages.clear();
    }
}
