package be.kdg.schelderadar.out.report;

import be.kdg.schelderadar.domain.ship.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: michaelkees
 * Date: 14/11/15
 */
public class ActionGenerator implements ActionCaller {
    Map<String, String> typeAction = new HashMap<>();

    public ActionGenerator(Map<String, String> typeAction) {
        this.typeAction = typeAction;
    }

    @Override
    public String getAction(String type, Boolean dangerousCargo) {
        if (!typeAction.get(type).isEmpty()) {
            if (dangerousCargo) {
                return typeAction.get("dangerous");
            } else {
                return typeAction.get(type);
            }
        }
        return null;
    }

    @Override
    public void addTypeAction(String type, String action) {
        typeAction.put(type, action);
    }
}
