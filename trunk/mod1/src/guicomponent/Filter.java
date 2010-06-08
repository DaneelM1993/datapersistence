/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guicomponent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wizard1993
 */
public abstract class Filter<T> {

    List<String> Field;
    protected Map<String, Method> methods = new TreeMap<String, Method>();
    String className;
    List<Condition<T>> conditions = new ArrayList<Condition<T>>();
    List<Operator<T>> operators = new ArrayList<Operator<T>>();
    Map<String, Wrapper> fieldWrapper = new HashMap<String, Wrapper>();

    public Filter(String s) {
        try {
            className = s;
            loadMethods();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Filter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Filter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getClassName() {
        return className;
    }

    public void add(Condition<T> c) {
        conditions.add(c);
    }

    public void add(Operator<T> o) {
        operators.add(o);
    }

    protected List<String> loadFields() throws ClassNotFoundException, SecurityException {
        List<String> fields;
        if (Field == null) {
            fields = new ArrayList<String>();
            List<Field> f = new ArrayList<Field>();
            f = new ArrayList<Field>(Arrays.asList(Class.forName(className).getDeclaredFields()));
            fields = new ArrayList<String>();
            for (int i = 0; i < f.size(); i++) {
                fields.add(f.get(i).getName());
            }
        } else {
            fields = this.Field;
        }
        return fields;
    }

    protected void loadMethods() throws ClassNotFoundException, SecurityException {
        methods.clear();
        for (Method m : Class.forName(className).getMethods()) {
            methods.put(m.getName().toUpperCase(), m);
        }
    }

    protected Object getValue(String s, Object invoker) throws Exception {
        if (methods.containsKey(("get" + s).toUpperCase())) {
            return methods.get(("get" + s).toUpperCase()).invoke(invoker, new Object[0]);

        } else if (methods.containsKey(("is" + s).toUpperCase())) {

            return methods.get(("is" + s).toUpperCase()).invoke(invoker, new Object[0]);

        }
        System.err.println("field:" + s + " not found");
        return null;
    }

    protected abstract T save();

    private void setValue(String field, Object value, T target) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String m = ("set" + field).toUpperCase();
        if (methods.containsKey(m)) {
            methods.get(m).invoke(target, value);
        }

    }

    public void addFieldWrapper(String f, Wrapper w) {
        fieldWrapper.put(f, w);
    }

    protected boolean existSetter(String string) {

        return methods.containsKey(("set" + string).toUpperCase());
    }

    protected void setViaReflection(String field, T t, String source) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        Class para[] = methods.get(("set"+field).toUpperCase()).getParameterTypes();
        if(para.length!=1)throw new NoSuchMethodError();
        Class formType=para[0];
        Constructor c = formType.getConstructor(String.class);
        if (c != null) {
            Object fieldValue = c.newInstance(source);
            setValue(field, fieldValue, t);
        }
    }

    protected void setViaWrapper(String string, T target, String source) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        setValue(string, fieldWrapper.get(string).fromString(source), target);
    }

    public void setField(String fieldName, T target, String source) throws UnsupportedOperationException {
        try {
            if (fieldWrapper.containsKey(fieldName)) {
                setViaWrapper(fieldName, target, source);
            } else if (existSetter(fieldName)) {
                setViaReflection(fieldName, target, source);
            } else {
                throw new UnsupportedOperationException("impossibile to persist this field:" + fieldName + "\n please create a wrapper");
            }
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        } catch (NoSuchFieldException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        } catch (SecurityException securityException) {
            securityException.printStackTrace();
        } catch (NoSuchMethodException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
        } catch (InstantiationException instantiationException) {
            instantiationException.printStackTrace();
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        } catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
        } catch (InvocationTargetException invocationTargetException) {
            invocationTargetException.printStackTrace();
        }
    }
}
