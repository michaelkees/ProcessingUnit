package be.kdg.schelderadar.domain.message;

import java.util.Date;

/**
 * User: michaelkees
 * Date: 04/11/15
 */
public class ShipMessage {
    int shipId;
    Date timestamp;


    public ShipMessage(int shipId, Date timestamp) {
        this.shipId = shipId;
        this.timestamp = timestamp;
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
}
