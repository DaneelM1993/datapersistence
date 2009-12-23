/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.Serializer;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

/**
 *
 * @author Wizard1993
 */
public class CastorSerializer implements Serializer {
    CharArrayWriter w = new CharArrayWriter();

    @Override
    public String toString(Serializable t) {


        try {
            Marshaller.marshal(t, w);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        w.reset();
        return new String(w.toCharArray());
    }

    @Override
    public Serializable fromString(String str) {
        try {
            return (Serializable) Unmarshaller.unmarshal(Serializable.class, new StringReader(str));
        } catch (MarshalException ex) {
            Logger.getLogger(CastorSerializer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ValidationException ex) {
            Logger.getLogger(CastorSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
