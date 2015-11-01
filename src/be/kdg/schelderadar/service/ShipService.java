package be.kdg.schelderadar.service;

import be.kdg.schelderadar.domain.ShipInfo;

/**
 * User: michaelkees
 * Date: 01/11/15
 */
public interface ShipService {
    ShipInfo getShipInfo(int shipId) throws ShipServiceException;
}
