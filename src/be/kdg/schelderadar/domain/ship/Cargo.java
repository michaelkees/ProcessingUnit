package be.kdg.schelderadar.domain.ship;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class Cargo {
    private String type;
    private int amount;

    public Cargo(String cargoID, int cargoWeight) {
        this.type = cargoID;
        this.amount = cargoWeight;
    }


    public Cargo() {
    }

    public String getCargoID() {

        return type;
    }

    public void setCargoID(String cargoID) {
        this.type = cargoID;
    }

    public int getCargoWeight() {
        return amount;
    }

    public void setCargoWeight(int cargoWeight) {
        this.amount = cargoWeight;
    }
}
