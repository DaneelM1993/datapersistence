/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence.Serializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import java.io.Serializable;

/**
 *
 * @author Wizard1993
 */
public class JSONSerializer implements Serializer{
    XStream xStream;

    public JSONSerializer() {
         xStream = new XStream(new JettisonMappedXmlDriver());

    }

    @Override
    public String toString(Serializable t) {
        return xStream.toXML(t);
    }

    @Override
    public Serializable fromString(String str) {
        return (Serializable) xStream.fromXML(str);
    }

}
