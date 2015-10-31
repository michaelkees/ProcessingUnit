import be.kdg.schelderadar.in.RabbitMQReceiver;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class RabbitMqTest {
    public static void main(String[] args) {
        RabbitMQReceiver rabbitMq = new RabbitMQReceiver();
        try {
            rabbitMq.initialize();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (TimeoutException e) {
            System.out.println(e.getMessage());
        }
    }
}
