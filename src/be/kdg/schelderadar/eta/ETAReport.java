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
    StringBuilder shipETAdata;
    Date timestamp;

    public ETAReport() {
        this.timestamp = new Date();
        shipETAdata = new StringBuilder();
    }


    public void addShipETAData(Ship ship, Long eta){
        shipETAdata.append(ship.getShipId());
        shipETAdata.append(" ");
        shipETAdata.append(String.valueOf(eta));
        shipETAdata.append("\n");
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("ETA data: %s \n %s", df.format(timestamp), shipETAdata.toString());
    }
}
