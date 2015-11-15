package be.kdg.schelderadar.domain.message;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

/**
 * User: michaelkees
 * Date: 04/11/15
 */
public interface MessageConverter {
    /**
     *
     * @param object
     * @return
     * @throws MarshalException
     * @throws ValidationException
     */
    String convertJavaToXML(Object object) throws MarshalException, ValidationException;

    /**
     *
     * @param XMLmessage
     * @param classType
     * @return
     * @throws MarshalException
     * @throws ValidationException
     */
    Object convertXMLToJava(String XMLmessage, String classType) throws MarshalException, ValidationException;
}
