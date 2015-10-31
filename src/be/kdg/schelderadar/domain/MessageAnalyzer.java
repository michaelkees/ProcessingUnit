package be.kdg.schelderadar.domain;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public interface MessageAnalyzer {
    void analyzeMessage(PositionMessage message);
    void analyzeMessage(IncidentMessage message);


}
