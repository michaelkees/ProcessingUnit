package be.kdg.schelderadar.broker;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class MQException extends Exception{
    public MQException(String message, Throwable cause) {
        super(message, cause);
    }
}
