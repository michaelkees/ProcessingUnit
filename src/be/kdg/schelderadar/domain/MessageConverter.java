package be.kdg.schelderadar.domain;

import com.sun.tools.classfile.Type;
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
    public  String convertXMLJava(Object object) throws MarshalException, ValidationException {
        Writer writer= new StringWriter();
        Marshaller.marshal(object, writer);
        return writer.toString();
    }

    public  Object convertJavaXML(String XMLmessage, Class classType) throws MarshalException, ValidationException {
        Reader reader = new StringReader(XMLmessage);
        Object object = Unmarshaller.unmarshal(classType.getClass(), reader);
        //more options
        return object;
    }

}
