/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guicomponent;

import java.util.Vector;
import persistence.local.AbstractProxy;
import persistence.local.UpdateEvent;
import persistence.local.updateEventListener;

/**
 *
 * @author Wizard1993
 */
public class DataPool implements updateEventListener{
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

    @Override
    public void UpdateEventPerformed(UpdateEvent ev) {
        AbstractProxy ap=(AbstractProxy) ev.getSource();
        Object o=ap.map.get(ev.getId());
        UpdateData(o);
    }

}
