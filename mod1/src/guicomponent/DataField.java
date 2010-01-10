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
public class DataField<T> extends javax.swing.JTextField implements GuiObject {

    Binder<T> binder;

    public DataField() {
        
    }

    public void setData(final T t) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setText(binder.StringValue(t));
            }
        });
    }

    @Override
    public void Bind(String Field) {
        binder = new Binder<T>(Field);
    }

}
