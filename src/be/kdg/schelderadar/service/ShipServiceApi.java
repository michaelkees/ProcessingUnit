package be.kdg.schelderadar.service;

import be.kdg.schelderadar.domain.ship.Cargo;
import be.kdg.schelderadar.domain.JsonHandler;
import be.kdg.schelderadar.domain.ship.ShipInfo;
import be.kdg.se3.proxy.ShipServiceProxy;
import org.apache.log4j.Logger;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.IOException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ShipServiceApi implements ShipService {
    private ShipServiceProxy proxy;
    private String url;
    private final static Logger logger = Logger.getLogger(ShipServiceApi.class);


    public ShipServiceApi(String url) {
        proxy = new ShipServiceProxy();
        this.url = url;
    }

    public ShipInfo getShipInfo(int shipId) throws ShipServiceException {
        try {
            String shipInfoJson = proxy.get(url + shipId);
            JsonObject shipObject = JsonHandler.handleJSON(shipInfoJson);

            if(shipObject.containsKey("error")){
                logger.warn("Error message during converting json to ShipInfo-Object");
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
            logger.error("Error during converting message to ShipInfo");
            throw new ShipServiceException(e.getMessage(), e);
        }
    }
}
