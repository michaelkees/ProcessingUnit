package be.kdg.schelderadar.service;

import be.kdg.schelderadar.domain.ship.ShipInfo;

/**
 * User: michaelkees
 * Date: 01/11/15
 */
public interface ShipService {
    /**
     * getting the ship information (proxy service)
     * @param shipId
     * @return
     * @throws ShipServiceException
     */
    ShipInfo getShipInfo(int shipId) throws ShipServiceException;
}
