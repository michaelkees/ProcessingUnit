package be.kdg.schelderadar.out.report;

import be.kdg.schelderadar.domain.ship.Ship;

/**
 * User: michaelkees
 * Date: 14/11/15
 */
public interface ActionCaller {
    /**
     * Getting an action for a type.
     * (If dangerousCargo true --> 'AlleSchepenVoorAnker')
     * @param type
     * @param dangerousCargo
     * @return
     */
    String getAction(String type, Boolean dangerousCargo);

    /**
     * adding an action for a type. (adjustable list)
     * @param type
     * @param action
     */
    void addTypeAction(String type, String action);
}
