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
import java.util.*;
import javax.print.DocFlavor.STRING;
import javax.swing.SwingUtilities;


/**
 *
 * @author wizard1993
 */
public class Derbyproxy<T extends Serializable> extends  AbstractProxy<T> {
    String pathofbackup;
    Set<updateEventListener> compToNotify = Collections.synchronizedSet(new HashSet<updateEventListener>());
    public Derbyproxy(String table,Information information) {
        System.out.println("network init "+table);
        map = new Derbymap<T>(table, information);
        pathofbackup=table+".xml";
    }
    public Derbyproxy<T> init(){
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
    public void NotifyUpdate(final int id,final String state) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (updateEventListener eventListener : compToNotify) {
                    eventListener.UpdateEventPerformed(new UpdateEvent(this, id, UpdateEvent.State.valueOf(state)));

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
        ((Derbymap)map).lockValues(Key);
    }

    @Override
    public void releaseLock(Integer key) {
       ((Derbymap)map).releaseLock(key);
    }

    @Override
    public void dispose() {
        try {
            ((Derbymap) map).close();
        } catch (SQLException ex) {
            Logger.getLogger(Derbyproxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    
}
