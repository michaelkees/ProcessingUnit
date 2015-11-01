package be.kdg.schelderadar.domain;

import java.util.*;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ShipBuffer {
    private final ArrayList<Ship> shipList = new ArrayList<>();
    private int timeInterruptShipBuffering;
    private Map<Ship, Long> timeShipLastSignal = new TreeMap<>();

    public ShipBuffer(int timeInterruptShipBuffering) {
        this.timeInterruptShipBuffering = timeInterruptShipBuffering;
    }

    public void addShip(Ship ship) {
        if (!shipList.contains(ship)) {
            shipList.add(ship);
            long currentTime = new Date().getTime();
            timeShipLastSignal.put(ship, currentTime);
        }
    }

    public Collection<Ship> getShipList() {
        return shipList;
    }

    public void updateShip(Ship ship, int afstandTotLoskade) {
        for (Ship s : shipList) {
            if (s.equals(ship)) {
                s.setAfstandTotLoskade(afstandTotLoskade);
                long currentTime = new Date().getTime();
                timeShipLastSignal.replace(s, currentTime);
            }
        }
    }

    public Ship getShip(int shipId) {
        for (Ship s : shipList) {
            if (s.getShipId() == shipId) {
                return s;
            }
        }
        return null;
    }

    public void addShipInfo(ShipInfo shipInfo, Ship ship) {
        for (Ship s : shipList) {
            if (s.equals(ship)) {
                s.setShipInfo(shipInfo);
            }
        }
    }

    public Boolean hasShipInfo(Ship ship) {
        return ship.getShipInfo() != null;
    }

    public Boolean exitsShip(int shipId) {
        for (Ship s : shipList) {
            if (s.getShipId() == shipId) {
                return true;
            }
        }
        return false;
    }

    public void checkSignalShip() {
        ArrayList<Ship> noSignalShips = new ArrayList<>();
        if (!shipList.isEmpty()) {

            for (Ship s : shipList) {
                long shipLastSignalTime = timeShipLastSignal.get(s);
                long currentTime = new Date().getTime();

                if (shipLastSignalTime + timeInterruptShipBuffering < currentTime) {
                    noSignalShips.add(s);
                }
            }

            for(Ship s : noSignalShips){
                shipList.remove(s);
            }

        }
    }

    @Override
    public String toString() {
        String list = "";
        for (Ship s : shipList) {
            list += String.format("shipId: %s afstand: %d\n", s.getShipId(), s.getAfstandTotLoskade());
        }
        return list;
    }
}
