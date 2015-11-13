package be.kdg.schelderadar.eta;

import be.kdg.schelderadar.domain.ship.Ship;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: michaelkees
 * Date: 02/11/15
 */
public class ETAReport {
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    String shipETAdata="";
    Date timestamp;

    public ETAReport() {
        this.timestamp = new Date();

    }

    public void addShipETAData(Ship ship, Long eta){
        shipETAdata += String.format("ship: %10d | ETA: %10d seconden\n", ship.getShipId(), eta);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("ETA data: %s \n%s", df.format(timestamp), shipETAdata);
    }
}
