package be.kdg.schelderadar.domain;

/**
 * User: michaelkees
 * Date: 14/11/15
 */
public class ProcessingException extends Exception {
    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingException(String message) {
        super(message);
    }
}
