package be.kdg.schelderadar.domain.message;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

/**
 * User: michaelkees
 * Date: 04/11/15
 */
public interface MessageConverter {
    String convertJavaToXML(Object object) throws MarshalException, ValidationException;
    Object convertXMLToJava(String XMLmessage, String classType) throws MarshalException, ValidationException;
}
