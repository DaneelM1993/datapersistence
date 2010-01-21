/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author wizard1993
 */
public abstract class AbstractProxy<T>{

    public Map<Integer, T> map = null;
    private Random seed = new Random();
    private Set<Integer> set=new HashSet<Integer>();

    public abstract void AddComponentToNotify(updateEventListener uel);

    public abstract void lockValues(Integer Key) throws IllegalAccessException;

    public abstract void releaseLock(Integer key);

    public abstract void LoadValues();

    public abstract void NotifyUpdate(int id,UpdateEvent.State state);

    public abstract void RemoveComponentToNotify(updateEventListener uel);

    public abstract void commit();

    public abstract void dispose();

    public Integer generateKey() {        
        Integer i = seed.nextInt();
        while (map.containsKey(i)) {
            System.err.println(i);
            i = seed.nextInt();
        }        
        return i;
    }
}
