package be.kdg.schelderadar.eta;

import be.kdg.schelderadar.domain.message.PositionMessage;
import be.kdg.schelderadar.domain.ship.Ship;

import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;

/**
 * User: michaelkees
 * Date: 14/11/15
 */
public interface ETACaller {
    /**
     *
     * @param shipMap
     * @return
     */
    NavigableMap<Ship, Long> calculateEstimatedTimeOfArrivalsMap(Map<Ship, ArrayList<PositionMessage>> shipMap);

    /**
     *
     * @param shipMap
     * @return
     */
    Boolean analyzeSpeedShip(NavigableMap<Ship, ArrayList<PositionMessage>> shipMap);

}
