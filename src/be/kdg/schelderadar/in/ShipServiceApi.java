package be.kdg.schelderadar.in;

import be.kdg.schelderadar.domain.Cargo;
import be.kdg.schelderadar.domain.JSONHandler;
import be.kdg.schelderadar.domain.ShipInfo;
import be.kdg.schelderadar.domain.ShipServiceException;
import be.kdg.se3.proxy.ShipServiceProxy;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.IOException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ShipServiceApi {
    private ShipServiceProxy proxy;
    private String url;

    public ShipServiceApi(String url) {
        proxy = new ShipServiceProxy();
        this.url = url;
    }

    public ShipInfo getShipInfo(int shipId) throws ShipServiceException {
        try {

            String shipInfoJson = proxy.get(url + shipId);
            JsonObject shipObject = JSONHandler.handleJSON(shipInfoJson);

            if(shipObject.containsKey("error")){
                throw new ShipServiceException(shipObject.getString("description"), new Throwable(shipObject.getString("error")));
            } else {
                ShipInfo shipInfo = new ShipInfo();

                shipInfo.setIMO(shipObject.getString("IMO"));
                shipInfo.setDangereousCargo(shipObject.getBoolean("dangereousCargo"));
                shipInfo.setNumberOfPassengers(shipObject.getInt("numberOfPassangers"));

                Cargo[] cargoList = new Cargo[shipObject.getJsonArray("cargo").size()];
                int counter=0;
                for(JsonValue jsonCargo : shipObject.getJsonArray("cargo")){
                    Cargo cargo = new Cargo();
                    JsonObject jsonObjectCargo = (JsonObject) jsonCargo;
                    cargo.setCargoID(jsonObjectCargo.getString("type"));
                    cargo.setCargoWeight(jsonObjectCargo.getInt("amount"));
                    cargoList[counter++] = cargo;
                }
                shipInfo.setCargo(cargoList);
                return shipInfo;
            }
        } catch (IOException e) {
            throw new ShipServiceException(e.getMessage(), e);
        }
    }
}
