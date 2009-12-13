/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence.network;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 *
 * @author Wizard1993
 */
public class XMLSerializier implements Serializer{

    @Override
    public String toString(Serializable t) {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        XMLEncoder enc=new XMLEncoder(out);
        enc.writeObject(t);
        enc.flush();
        enc.close();
        String str=new String(out.toByteArray());
        return str;
    }

    @Override
    public Serializable fromString(String str) {
        ByteArrayInputStream aais=new ByteArrayInputStream(str.getBytes());
        XMLDecoder dec=new XMLDecoder(aais);
        Serializable t=  (Serializable) dec.readObject();
        dec.close();
        return t;
    }

}
