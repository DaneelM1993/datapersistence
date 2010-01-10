/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guicomponent;

import javax.swing.SwingUtilities;

/**
 *
 * @author Wizard1993
 */
public class DataField<T> extends javax.swing.JTextField implements GuiElement {

    OneFieldBinder<T> binder;

    public DataField() {
        
    }
    @Override
    public void setData(final Object t) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setText(binder.StringValue((T) t));
            }
        });
    }

    @Override
    public GuiElement Bind(String Field) {
        binder = new OneFieldBinder<T>(Field);
        return this;
    }

    @Override
    public String getFieldName() {
        return binder.getFieldName();
    }

}
