package be.kdg.schelderadar.out;

import be.kdg.schelderadar.domain.PositionMessage;

/**
 * User: michaelkees
 * Date: 01/11/15
 */
public interface MessageStorage {
    void saveMessage(PositionMessage ps);
}
