package be.kdg.schelderadar.domain;

import java.util.Date;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class PositionMessage {
    private int shipId;
    private int centraleId;
    private Date Timestamp;
    private int afstandTotLoskade;

    public PositionMessage(int shipId, int centraleId, Date timestamp, int afstandTotLoskade) {
        this.shipId = shipId;
        this.centraleId = centraleId;
        Timestamp = timestamp;
        this.afstandTotLoskade = afstandTotLoskade;
    }

    public int getShipId() {
        return shipId;
    }

    public void setShipId(int shipId) {
        this.shipId = shipId;
    }

    public int getCentraleId() {
        return centraleId;
    }

    public void setCentraleId(int centraleId) {
        this.centraleId = centraleId;
    }

    public Date getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(Date timestamp) {
        Timestamp = timestamp;
    }

    public int getAfstandTotLoskade() {
        return afstandTotLoskade;
    }

    public void setAfstandTotLoskade(int afstandTotLoskade) {
        this.afstandTotLoskade = afstandTotLoskade;
    }
}
