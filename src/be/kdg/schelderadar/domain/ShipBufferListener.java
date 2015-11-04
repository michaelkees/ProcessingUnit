package be.kdg.schelderadar.domain;

import be.kdg.schelderadar.domain.ship.Ship;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public interface ShipBufferListener {
    /**
     *
     * @param ship
     */
    void  updateShip(Ship ship, int distance);
}
