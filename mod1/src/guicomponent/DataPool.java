/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guicomponent;

import java.util.Vector;

/**
 *
 * @author Wizard1993
 */
public class DataPool {
    Vector<GuiElement> vec=new Vector<GuiElement>();
    public void addGuiElement(GuiElement go){
        vec.add(go);
    }
    public void removeElement(GuiElement go){
        vec.remove(go);
    }
    public void UpdateData(Object o){
        for (GuiElement guiObject : vec) {
            guiObject.setData(o);
        }
    }

}
