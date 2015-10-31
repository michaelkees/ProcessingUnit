package be.kdg.schelderadar.domain;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class Ship {
    private int Id;
    private ShipInfo shipInfo;

    public Ship() {
    }

    public Ship(int id, ShipInfo shipInfo) {
        Id = id;
        this.shipInfo = shipInfo;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public ShipInfo getShipInfo() {
        return shipInfo;
    }

    public void setShipInfo(ShipInfo shipInfo) {
        this.shipInfo = shipInfo;
    }
}
