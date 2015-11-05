package be.kdg.schelderadar.out.report;

import be.kdg.schelderadar.domain.ship.Ship;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class IncidentReport {
    Ship ship;
    String type;
    String action;

    public IncidentReport() {
    }

    public IncidentReport(Ship ship, String type, String action) {
        this.ship = ship;
        this.type = type;
        this.action = action;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
