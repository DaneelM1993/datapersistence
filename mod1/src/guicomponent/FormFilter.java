/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guicomponent;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;
import persistence.local.AbstractProxy;

/**
 *
 * @author wizard1993
 */
public class FormFilter<T> extends Filter<T> {

    T target;
    Map<String, SwingWrapper> formFields = new HashMap<String, SwingWrapper>();
    final AbstractProxy<T> absp;

    public FormFilter(String s, AbstractProxy p) {
        super(s);
        absp = p;
    }

    public void addComponent(String field, JTextComponent comp) {
        formFields.put(field, new JTextComponentWrapper(comp));
    }
    public void addComponent(String field, JComboBox comp) {
        formFields.put(field, new JComboBoxWrapper(comp));
    }
    public void loadElem(T t) {
        loadElem(t, t.hashCode());
    }

    private void loadElem(T t, Integer hash) {
        try {
            T elem;
            if (absp.map.containsKey(hash)) {
                elem = absp.map.get(hash);
            } else {
                elem = t;
            }
            loadMethods();
            loadFields();
            target = elem;
            setFormField(elem);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public T save() {
        setValues();
        absp.map.put(target.hashCode(), target);
        absp.commit();
        return target;
    }

    protected T setValues() {
        try {
            if (target == null) {
                target = (T) Class.forName(className).newInstance();
            }
            for (String string : formFields.keySet()) {
                setField(string, target, formFields.get(string).getText());
            }
            return target;
        } catch (InstantiationException ex) {
            Logger.getLogger(FormFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FormFilter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void setFormField(T elem) throws Exception {
        for (String string : formFields.keySet()) {
            System.out.println(string);
            Object o = getValue(string, elem);
            if (o != null) {
                if (fieldWrapper.containsKey(string)) {
                    formFields.get(string).setText(fieldWrapper.get(string).toString(o));
                } else {
                    formFields.get(string).setText(o.toString());
                }
            }
            else
                formFields.get(string).setText("");

        }
    }
}

abstract class SwingWrapper {

    abstract String getText();
    abstract void setText(String str);
}

class JTextComponentWrapper extends SwingWrapper {

    JTextComponent jtc;

    public JTextComponentWrapper(JTextComponent jtc) {
        this.jtc = jtc;
    }

    @Override
    String getText() {
        return jtc.getText();
    }

    @Override
    void setText(String str) {
        jtc.setText(str);
    }
}
class JComboBoxWrapper extends SwingWrapper{
    JComboBox jcb;
    HashMap<String,Integer> options;
    public JComboBoxWrapper(JComboBox jcb) {
        this.jcb = jcb;
        options=new HashMap<String, Integer>(jcb.getItemCount()+5);
    }

    @Override
    String getText() {
        return jcb.getItemAt(jcb.getSelectedIndex()).toString();
    }

    @Override
    void setText(String str) {
        for (int i = 0; i < jcb.getItemCount(); i++) {
            options.put(jcb.getItemAt(i).toString(), i);

        }
    }
    
}
