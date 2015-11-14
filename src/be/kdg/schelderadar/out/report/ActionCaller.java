package be.kdg.schelderadar.out.report;

import be.kdg.schelderadar.domain.ship.Ship;

/**
 * User: michaelkees
 * Date: 14/11/15
 */
public interface ActionCaller {
    String getAction(String type, Boolean dangerousCargo);
    void addTypeAction(String type, String action);
}
