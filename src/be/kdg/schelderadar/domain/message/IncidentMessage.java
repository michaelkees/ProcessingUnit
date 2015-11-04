package be.kdg.schelderadar.domain.message;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class IncidentMessage {
    private int shipId;
    private String action;

    public IncidentMessage(int shipId, String action) {
        this.shipId = shipId;
        this.action = action;
    }

    public int getShipId() {
        return shipId;
    }

    public void setShipId(int shipId) {
        this.shipId = shipId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
