package be.kdg.schelderadar.out;

import be.kdg.schelderadar.domain.PositionMessage;

/**
 * User: michaelkees
 * Data Storage --> messages from generator --> System println
 */
public class ShipMessageStorage implements MessageStorage {

    public ShipMessageStorage() {
    }

    @Override
    public void saveMessage(PositionMessage ps) {
        System.out.println("store: " + ps.getShipId());
    }
}
