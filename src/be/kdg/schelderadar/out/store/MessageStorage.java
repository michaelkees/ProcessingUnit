package be.kdg.schelderadar.out.store;

import be.kdg.schelderadar.domain.message.PositionMessage;

/**
 * User: michaelkees
 * Date: 01/11/15
 */
public interface MessageStorage {
    void saveMessage(Object message, String classType);
}
