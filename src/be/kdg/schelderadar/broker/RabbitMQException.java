package be.kdg.schelderadar.broker;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class RabbitMQException extends Exception{
    public RabbitMQException(String message, Throwable cause) {
        super(message, cause);
    }
}