package be.kdg.schelderadar.domain.message;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

/**
 * User: michaelkees
 * Date: 04/11/15
 */
public interface MessageConverter {
    /**
     *  converting an object to xml with the marshaller
     * @param object
     * @return
     * @throws MarshalException
     * @throws ValidationException
     */
    String convertJavaToXML(Object object) throws MarshalException, ValidationException;

    /**
     * converting the xml tot an object with the unmarshaller
     * @param XMLmessage
     * @param classType
     * @return
     * @throws MarshalException
     * @throws ValidationException
     */
    Object convertXMLToJava(String XMLmessage, String classType) throws MarshalException, ValidationException;
}
