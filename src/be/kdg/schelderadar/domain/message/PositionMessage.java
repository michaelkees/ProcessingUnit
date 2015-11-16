package be.kdg.schelderadar.domain.message;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * User: michaelkees
 * Date: 31/10/15
 */

public class PositionMessage {
    private int shipId;
    private Date timestamp;
    private String centraleId;
    private int afstandTotLoskade;

    public PositionMessage() {
    }

    public PositionMessage(int shipId, Date timestamp, String centraleId, int afstandTotLoskade) {
        this.shipId = shipId;
        this.timestamp = timestamp;
        this.centraleId = centraleId;
        this.afstandTotLoskade = afstandTotLoskade;
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

    public String getCentraleId() {
        return centraleId;
    }

    public void setCentraleId(String centraleId) {
        this.centraleId = centraleId;
    }

    public int getAfstandTotLoskade() {
        return afstandTotLoskade;
    }

    public void setAfstandTotLoskade(int afstandTotLoskade) {
        this.afstandTotLoskade = afstandTotLoskade;
    }
}
