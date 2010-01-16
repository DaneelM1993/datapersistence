/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guicomponent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wizard1993
 */
public class Row {
    String names[];
    Vector<Field> vec=new Vector<Field>();
    Object o=new Object();
    public Row(String[] names) {
        this.names = names;
    }

    public void setData(Object o) {
        this.o = o;
    }

    public Vector<Field> getData(){
        
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
    public void setValue(int col,Object o){
        try {
            vec.get(col).set(this.o, o);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Row.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Row.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
