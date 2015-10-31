import be.kdg.schelderadar.domain.ShipServiceException;
import be.kdg.schelderadar.in.ShipServiceApi;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class ShipServiceTest {
    public static void main(String[] args) {
        ShipServiceApi serviceApi = new ShipServiceApi("www.services4se3.com/shipservice/");
        try {
            System.out.println(serviceApi.getShipInfo(1111111));
        } catch (ShipServiceException e) {
            System.out.println(e.getMessage());
        }

    }

}
