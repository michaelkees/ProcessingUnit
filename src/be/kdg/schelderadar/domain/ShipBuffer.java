package be.kdg.schelderadar.domain;

import be.kdg.schelderadar.cache.ShipMessageMapper;
import be.kdg.schelderadar.domain.ship.Ship;
import be.kdg.schelderadar.domain.ship.ShipInfo;

import java.util.*;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ShipBuffer {
    private ShipMessageMapper shipMessageMapper;
    private final ArrayList<Ship> shipList = new ArrayList<>();
    private Map<Ship, Long> timeShipLastSignal = new TreeMap<>();
    private int timeInterruptShipBuffering;

    public ShipBuffer(int timeInterruptShipBuffering, ShipMessageMapper shipMessageMapper) {
        this.timeInterruptShipBuffering = timeInterruptShipBuffering;
        this.shipMessageMapper = shipMessageMapper;
    }


    public void addShip(Ship ship) {
        if (!shipList.contains(ship)) {
            shipList.add(ship);
            long currentTime = new Date().getTime();
            timeShipLastSignal.put(ship, currentTime);
        }
    }

    public void updateShip(Ship ship, int afstandTotLoskade) {
        for (Ship s : shipList) {
            if (s.getShipId() == ship.getShipId()) {
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

    public Boolean existsShip(int shipId) {
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
            for (Ship s : noSignalShips) {
                shipList.remove(s);
            }

        }
    }

    public Collection<Ship> getShipList() {
        return shipList;
    }
    
    public Boolean hasShipInfo(Ship ship) {
        return ship.getShipInfo() != null;
    }

    public ShipMessageMapper getShipMessageMapper() {
        return shipMessageMapper;
    }

    public void setShipMessageMapper(ShipMessageMapper shipMessageMapper) {
        this.shipMessageMapper = shipMessageMapper;
    }

    public int getTimeInterruptShipBuffering() {
        return timeInterruptShipBuffering;
    }

    public void setTimeInterruptShipBuffering(int timeInterruptShipBuffering) {
        this.timeInterruptShipBuffering = timeInterruptShipBuffering;
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
