package be.kdg.schelderadar.domain.message;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

/**
 * User: michaelkees
 * Date: 03/11/15
 */
public interface MessageAnalyzer {
    /**
     *
     * @param message
     * @throws MarshalException
     * @throws ValidationException
     */
    void analyzeMessage(String message) throws MarshalException, ValidationException;
}
