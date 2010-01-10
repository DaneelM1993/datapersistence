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
    Vector<GuiObject> vec=new Vector<GuiObject>();
    public void addGuiElement(GuiObject go){
        vec.add(go);
    }
    public void removeElement(GuiObject go){
        vec.remove(go);
    }

}
