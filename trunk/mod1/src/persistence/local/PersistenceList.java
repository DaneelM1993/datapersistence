/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * @author Wizard1993
 */
public class PersistenceList<T> implements Set<T>, Serializable {

    private transient AbstractProxy<T> proxy;
    private TreeSet<Integer> keys = new TreeSet<Integer>();

    public PersistenceList(AbstractProxy<T> proxy) {
        this.proxy = proxy;
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

    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        super.finalize();
    }

}


