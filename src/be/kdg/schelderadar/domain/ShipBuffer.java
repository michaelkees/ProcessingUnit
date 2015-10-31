package be.kdg.schelderadar.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ShipBuffer{
    private final ArrayList<Ship> shipList = new ArrayList<>();

    public ShipBuffer() {
    }

    public void addShip(Ship ship) {
        shipList.add(ship);
    }

    public Collection<Ship> getShipList() {
        return shipList;
    }


}
