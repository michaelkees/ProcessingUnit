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
     * calculating the estimated time of arrival for 1 - * ships.
     * shipMap is a map with ship keys and an arraylist as value
     * the arraylist contains postionmessages for the key (ship) -->
     * calculating the time between 2 positions of a ship.  (timestamp and distance)
     * @param shipMap
     * @return
     */
    NavigableMap<Ship, Long> calculateEstimatedTimeOfArrivalsMap(Map<Ship, ArrayList<PositionMessage>> shipMap);

    /**
     * calculating the time between 2 positions (timestamps). Checking if the ship is moving (ETA is lower than previous ETA)
     * @param shipMap
     * @return
     */
    Boolean analyzeSpeedShip(NavigableMap<Ship, ArrayList<PositionMessage>> shipMap);

}
