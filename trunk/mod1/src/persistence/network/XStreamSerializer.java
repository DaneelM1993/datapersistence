/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence.network;

import com.thoughtworks.xstream.XStream;
import java.io.Serializable;

/**
 *
 * @author Wizard1993
 */
public class XStreamSerializer implements Serializer{
    XStream xStream=new XStream();
    @Override
    public String toString(Serializable t) {
        return xStream.toXML(t);
    }

    @Override
    public Serializable fromString(String str) {
        return (Serializable) xStream.fromXML(str);
    }

}
