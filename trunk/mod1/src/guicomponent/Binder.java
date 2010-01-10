/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guicomponent;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wizard1993
 */
class Binder<T> {

    String fieldName;
    Field f;

    public Binder(String fieldName) {
        this.fieldName = fieldName;
    }

    

    public void bind(T t) {
        Class clazz = t.getClass();
        Field fields[] = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                f = field;
            }
        }
    }

    public String StringValue(T t) {
        if (f == null) {
            bind(t);
        }
        try {
            return f.get(t).toString();
        } catch (Exception ex) {
            Logger.getLogger(Binder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
