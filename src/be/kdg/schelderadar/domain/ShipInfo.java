package be.kdg.schelderadar.domain;

import java.util.Arrays;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ShipInfo {
    private String IMO;
    private boolean dangereousCargo;
    private int numberOfPassengers;
    private Cargo[] cargoList;

    public ShipInfo() {
    }

    public ShipInfo(String IMO, boolean dangereousCargo, int numberOfPassengers, Cargo[] cargoList) {
        this.IMO = IMO;
        this.dangereousCargo = dangereousCargo;
        this.numberOfPassengers = numberOfPassengers;
        this.cargoList = cargoList;
    }

    public String getIMO() {
        return IMO;
    }

    public void setIMO(String IMO) {
        this.IMO = IMO;
    }

    public boolean isDangereousCargo() {
        return dangereousCargo;
    }

    public void setDangereousCargo(boolean dangereousCargo) {
        this.dangereousCargo = dangereousCargo;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    public Cargo[] getCargos() {
        return cargoList;
    }

    public void setCargo(Cargo[] cargoList) {
        this.cargoList = cargoList;
    }
}
