package be.kdg.schelderadar.out.report;

/**
 * User: michaelkees
 * Date: 14/11/15
 */
public class ActionCallerException extends Exception{
    public ActionCallerException() {
    }

    public ActionCallerException(String message) {
        super(message);
    }

    public ActionCallerException(String message, Throwable cause) {
        super(message, cause);
    }
}
