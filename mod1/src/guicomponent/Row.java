/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guicomponent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 *
 * @author Wizard1993
 */
public class Row {
    String names[];
    Object o=new Object();
    public Row(String[] names) {
        this.names = names;
    }

    public void setData(Object o) {
        this.o = o;
    }

    public Vector getData(){
        Vector<Object> vec=new Vector<Object>();
        Class c=o.getClass();
        HashMap<String,Field> map=new HashMap<String, Field>();
        Field[] f=c.getDeclaredFields();
        for (Field field : f) {
            map.put(field.getName(),field);
        }
        for (String string : names) {
            vec.add(map.get(string));
        }
        return vec;
    }

    @Override
    public int hashCode() {
        return o.hashCode();
    }


}
