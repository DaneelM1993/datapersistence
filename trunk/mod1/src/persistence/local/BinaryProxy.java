/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 *
 * @author wizard1993
 */
public class BinaryProxy<T extends Serializable> extends  AbstractProxy<T> {

    public BinaryProxy() {
        map = new TreeMap<Integer, T>();
    }

    
    private Set<updateEventListener> comp = Collections.synchronizedSet(new HashSet<updateEventListener>());
    protected String path;
    @Override
    public void AddComponentToNotify(updateEventListener uel) {
        this.comp.add(uel);
    }
    @Override
    public void NotifyUpdate(final  int id,final UpdateEvent.State state) {
        //ln("dsa");
        new Thread(new Runnable() {
            private Iterable<updateEventListener> c = comp;
            @Override
            public void run() {
                for (updateEventListener eventListener : this.c) {
                    eventListener.UpdateEventPerformed(new UpdateEvent(this, 0, UpdateEvent.State.add));
                }
            }
        }).start();
    }

    @Override
    public void RemoveComponentToNotify(updateEventListener uel) {
        this.comp.remove(uel);
    }

    @Override
    public void commit() {
        try {
            ObjectOutputStream ois = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(this.path)));
            ois.writeObject(this.map);
            ois.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public BinaryProxy(String path) {
        this.path=path;
        LoadValues();
    }
    @Override
    public void LoadValues() {
        try {
            File f=new File(path);
            f.createNewFile();
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
            map = (TreeMap<Integer, T>) ois.readObject();
            ois.close();
            
            
        } catch (Exception ex) {
            ex.printStackTrace();
            map = new TreeMap<Integer, T>();
        }
    }

    @Override
    public void lockValues(Integer Key) throws IllegalAccessException {
        
    }

    @Override
    public void releaseLock(Integer key) {

    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
