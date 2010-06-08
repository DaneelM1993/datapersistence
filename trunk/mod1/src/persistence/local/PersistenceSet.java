/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * @author Wizard1993
 */
public class PersistenceSet<T> implements Set<T>, Serializable {

    private transient AbstractProxy<T> proxy;
    private TreeSet<Integer> keys = new TreeSet<Integer>();

    public PersistenceSet(AbstractProxy<T> proxy) {

        this.proxy = proxy;
        System.out.println(this.proxy);
        
    }

    public int size() {
        return keys.size();
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }

    public boolean contains(Object o) {
        return keys.contains(o.hashCode());
    }

    public Iterator<T> iterator() {
        return getValues().iterator();
    }

    public Object[] toArray() {
        return getValues().toArray();
    }

    public <T> T[] toArray(T[] a) {
        return getValues().toArray(a);
    }

    public boolean add(T e) {
        System.out.println(proxy);
        proxy.map.put(e.hashCode(), e);
        return keys.add(e.hashCode());
    }

    public boolean remove(Object o) {
        proxy.map.remove(o.hashCode());
        return true;
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T t : c) {
            add(t);
        }
        return true;
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {
        keys.clear();
    }

    private Vector<T> getValues() {
        Vector<T> v = new Vector<T>();
        for (Integer integer : keys) {
            v.add(proxy.map.get(integer));
        }
        return v;
    }

    @Override
    protected void finalize() throws Throwable {
        proxy.commit();        
    }
    public void refreshProxy(AbstractProxy<T> proxy){
        this.proxy=proxy;
    }

}


