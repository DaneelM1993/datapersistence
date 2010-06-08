/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author wizard1993
 */
public abstract class AbstractProxy<T> implements Provider{

    public Map<Integer, T> map = null;
    private Random seed = new Random();
    private Set<Integer> set=new HashSet<Integer>();



    public abstract void lockValues(Integer Key) throws IllegalAccessException;

    public abstract void releaseLock(Integer key);

    public abstract void LoadValues();



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

    @Override
    public List getDataList() {
        return Arrays.asList(map.values().toArray());
    }

}
