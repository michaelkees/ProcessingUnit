package be.kdg.schelderadar.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * User: michaelkees
 * Date: 31/10/15
 */

@XmlRootElement
public class PositionMessage {
    private int shipId;
    private String centraleId;
    private Date timestamp;
    private int afstandTotLoskade;


    public int getShipId() {
        return shipId;
    }

    public String getCentraleId() {
        return centraleId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getAfstandTotLoskade() {
        return afstandTotLoskade;
    }

    @XmlElement
    public void setShipId(int shipId) {
        this.shipId = shipId;
    }
    @XmlElement
    public void setCentraleId(String centraleId) {
        this.centraleId = centraleId;
    }
    @XmlElement
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    @XmlElement
    public void setAfstandTotLoskade(int afstandTotLoskade) {
        this.afstandTotLoskade = afstandTotLoskade;
    }
}
