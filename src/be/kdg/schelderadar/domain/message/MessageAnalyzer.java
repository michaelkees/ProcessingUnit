package be.kdg.schelderadar.domain.message;

import be.kdg.schelderadar.domain.message.IncidentMessage;
import be.kdg.schelderadar.domain.message.PositionMessage;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public interface MessageAnalyzer {
    void analyzeMessage(PositionMessage message);
    void analyzeMessage(IncidentMessage message);


}
