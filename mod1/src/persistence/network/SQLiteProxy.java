/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.network;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import persistence.local.*;
import java.io.Serializable;
import java.sql.Connection;
import java.util.*;
import javax.swing.SwingUtilities;


/**
 *
 * @author wizard1993
 */
public class SQLiteProxy<T extends Serializable> extends  AbstractProxy<T> {
    String pathofbackup;
    Set<updateEventListener> compToNotify = Collections.synchronizedSet(new HashSet<updateEventListener>());
    public SQLiteProxy(String table,Information information) {
        map = new SQLiteMap<T>(table, information);
        ((AbstractMap)map).setNotifier(this);
        System.out.println("network init "+table);
        pathofbackup=table+".xml";
    }
    public SQLiteProxy<T> init(){
        return this;
    }
    

    @Override
    public void AddComponentToNotify(updateEventListener uel) {
        compToNotify.add(uel);
    }

    @Override
    public void LoadValues() {
        map.isEmpty();//it cause the pre loading
    }

    @Override
    public void NotifyUpdate(final int id, final UpdateEvent.State state) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (updateEventListener eventListener : compToNotify) {
                    eventListener.UpdateEventPerformed(new UpdateEvent(this, id,state));

                }
            }
        });

    }

    @Override
    public void RemoveComponentToNotify(updateEventListener uel) {
        compToNotify.remove(uel);
    }

    @Override
    public void commit() {
        try {
            localBackup();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void localBackup() {

        XmlProxy<T> xml = new XmlProxy<T>(pathofbackup);
        xml.map.values();
        for (Integer idx : map.keySet()) {
            xml.map.put(idx, map.get(idx));
        }
        
        xml.commit();
    }

    @Override
    public void lockValues(Integer Key) throws IllegalAccessException {
        ((SQLiteMap)map).lockValues(Key);
    }

    @Override
    public void releaseLock(Integer key) {
       ((SQLiteMap)map).releaseLock(key);
    }

    @Override
    public void dispose() {
        try {
            ((SQLiteMap) map).close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    
}
