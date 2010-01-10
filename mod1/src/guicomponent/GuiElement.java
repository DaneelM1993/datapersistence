/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guicomponent;


/**
 *
 * @author Wizard1993
 */
public interface GuiElement {
    public void setData(Object o);
    public GuiElement Bind(String Field);
    public String getFieldName();

}
