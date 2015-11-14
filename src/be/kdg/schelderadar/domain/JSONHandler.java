package be.kdg.schelderadar.domain;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class JsonHandler {
    public static JsonObject handleJSON(String jsonString){
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return  jsonObject;
    }
}
