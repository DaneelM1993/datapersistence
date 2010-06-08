/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;

import java.util.List;

/**
 *
 * @author Wizard1993
 */
public interface Provider {

    public List getDataList();

    public void AddComponentToNotify(updateEventListener uel);

    public void NotifyUpdate(int id, UpdateEvent.State state);

    public void RemoveComponentToNotify(updateEventListener uel);
}
