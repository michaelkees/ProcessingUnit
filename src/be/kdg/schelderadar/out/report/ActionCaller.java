package be.kdg.schelderadar.out.report;

import be.kdg.schelderadar.domain.ship.Ship;

/**
 * User: michaelkees
 * Date: 14/11/15
 */
public interface ActionCaller {
    /**
     *
     * @param type
     * @param dangerousCargo
     * @return
     */
    String getAction(String type, Boolean dangerousCargo);

    /**
     *
     * @param type
     * @param action
     */
    void addTypeAction(String type, String action);
}
