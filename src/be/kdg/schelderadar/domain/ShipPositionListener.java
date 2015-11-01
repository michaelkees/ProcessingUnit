package be.kdg.schelderadar.domain;

import be.kdg.schelderadar.domain.model.Ship;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public interface ShipPositionListener {
    /**
     *
     * @param ship
     */
    void  updateShip(Ship ship);
}
