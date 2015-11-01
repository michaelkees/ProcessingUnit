package be.kdg.schelderadar.domain.model;

import java.util.Date;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class Ship implements Comparable<Ship>{
    private int shipId;
    private String centraleId;
    private Date timestamp;
    private int afstandTotLoskade;
    private ShipInfo shipInfo;

    public Ship(int shipId, String centraleId, Date timestamp, int afstandTotLoskade) {
        this.shipId = shipId;
        this.centraleId = centraleId;
        this.timestamp = timestamp;
        this.afstandTotLoskade = afstandTotLoskade;
    }

    public Ship() {
    }

    public int getShipId() {
        return shipId;
    }

    public void setShipId(int shipId) {
        this.shipId = shipId;
    }

    public String getCentraleId() {
        return centraleId;
    }

    public void setCentraleId(String centraleId) {
        this.centraleId = centraleId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getAfstandTotLoskade() {
        return afstandTotLoskade;
    }

    public void setAfstandTotLoskade(int afstandTotLoskade) {
        this.afstandTotLoskade = afstandTotLoskade;
    }

    public ShipInfo getShipInfo() {
        return shipInfo;
    }

    public void setShipInfo(ShipInfo shipInfo) {
        this.shipInfo = shipInfo;
    }

    @Override
    public int compareTo(Ship o) {
        return this.shipId - o.shipId;
    }
}
