package be.kdg.schelderadar.domain.message;

import be.kdg.schelderadar.domain.message.PositionMessage;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * User: michaelkees
 * Date: 31/10/15
 */
public class MessageConverter {
    public  String convertJavaToXML(Object object) throws MarshalException, ValidationException {
        Writer writer= new StringWriter();
        Marshaller.marshal(object, writer);
        return writer.toString();
    }

    public  Object convertXMLToJava(String XMLmessage, Object object) throws MarshalException, ValidationException {
        Reader reader = new StringReader(XMLmessage);
        return (Object) Unmarshaller.unmarshal(object.getClass(), reader);
        //more options
    }

    public  Object convertXMLToJava(String XMLmessage) throws MarshalException, ValidationException {
        Reader reader = new StringReader(XMLmessage);
        return (PositionMessage) Unmarshaller.unmarshal(PositionMessage.class, reader);
        //more options
    }

}
