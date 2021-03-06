package be.kdg.schelderadar.domain.message;

import java.util.Date;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class IncidentMessage{
    private int shipId;
    private Date timestamp;
    private String type;

    public IncidentMessage() {
    }

    public IncidentMessage(int shipId, Date timestamp, String type) {
        this.shipId = shipId;
        this.timestamp = timestamp;
        this.type = type;
    }

    public int getShipId() {
        return shipId;
    }

    public void setShipId(int shipId) {
        this.shipId = shipId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
