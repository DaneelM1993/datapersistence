/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.network;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import persistence.Serializer.Serializer;
import persistence.Serializer.XMLSerializier;
import persistence.local.AbstractProxy;
import persistence.local.Information;

/**
 *
 * @author Wizard1993
 */
public abstract class AbstractMap<V extends Serializable> implements Map<Integer, V> {

    protected static Connection connection;
    protected TreeMap<Integer, V> cachedValues = new TreeMap<Integer, V>();
    protected PreparedStatement containsvalue = null;
    protected boolean first = true;
    protected PreparedStatement getsize = null;
    protected PreparedStatement getxml = null;
    protected PreparedStatement insert = null;
    protected double lastUpdate;
    protected Integer lockedID;
    protected TreeMap<Integer, String> mapKeys = new TreeMap<Integer, String>();
    protected AbstractProxy<V> notifier;
    protected Serializer serializer = null;
    protected String table;
    protected PreparedStatement toUpdate = null;
    protected PreparedStatement updateps = null;
    protected Set<Integer> usedKey = new TreeSet<Integer>();

    protected V Deserialize(String xml) {
        if (cachedValues.containsKey(xml.hashCode())) {
            return (V) cachedValues.get(xml.hashCode());
        } else {
            V v = (V) serializer.fromString(xml);
            cachedValues.put(xml.hashCode(), v);
            return v;
        }
    }

    protected String Serialize(Object value) {
        if (mapKeys.containsKey(value.hashCode())) {
            return mapKeys.get(value.hashCode());
        } else {
            return serializer.toString((Serializable) value);
        }
    }

    protected Integer cache() {
        try {
            if (first) {
                ResultSet rs = HSQLDBMap.connection.createStatement().executeQuery("select * from " + table + ";");
                while (rs.next()) {
                    mapKeys.put(rs.getInt(1), rs.getString(2));
                }
                rs.close();
                first = false;
            } else {
                ResultSet rs = getRStocache();
                while (rs.next()) {
                    Integer i = rs.getInt(1);
                    mapKeys.put(i, getfromQuery(i));
                }
            }
            lastUpdate = System.currentTimeMillis();
        } catch (Exception e) {
        }
        return 0;
    }

    void checkLocked(Object key) throws IllegalAccessException {
        try {
            ResultSet isLocked = HSQLDBMap.connection.createStatement().executeQuery("select * from " + table + "temp where ID=" + key.hashCode());
            if (isLocked.next()) {
                throw new IllegalAccessException("the object is locked");
            }
        } catch (SQLException ex) {
            Logger.getLogger(HSQLDBMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void clear() {
        try {
            HSQLDBMap.connection.prepareStatement("delete from " + table).execute();
            mapKeys.clear();
            cachedValues.clear();
            first = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
        //connection.createStatement().execute("SHUTDOWN");
        HSQLDBMap.connection.close();
    }

    @Override
    public boolean containsKey(Object key) {
        boolean b = false;
        if (!usedKey.contains(key.hashCode())) {
            b = (get(key.hashCode()) != null);
        } else {
            b = true;
        }
        return b;
    }

    @Override
    public boolean containsValue(Object value) {
        return hasValue(value);
    }

    protected abstract void createTables();

    @Override
    public Set<Entry<Integer, V>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void finalize() throws Throwable {
        HSQLDBMap.connection.close();
        super.finalize();
    }

    @Override
    public V get(Object key) throws IllegalAccessError {
        try {
            String xml = getXML(key.hashCode());
            if (xml == null) {
                return null;
            }
            return Deserialize(xml);
        } catch (Exception ex) {
            Logger.getLogger(HSQLDBMap.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected ResultSet getRStocache() throws SQLException {
        ResultSet rs = null;
        rs = HSQLDBMap.connection.prepareCall("select (ID) from toupdate" + table + " where Time>" + lastUpdate).executeQuery();
        return rs;
    }

    protected int getSize() {
        int num = 0;
        try {
            ResultSet rs = getsize.executeQuery();
            if (rs.next()) {
                num = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    protected String getXML(Integer key) {
        String xml = null;
        try {
            if (mapKeys.containsKey(key)) {
                xml = mapKeys.get(key);
            } else {
                xml = getfromQuery(key);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return xml;
    }

    protected String getfromQuery(final Integer key) throws SQLException {
        String xml;
        getxml.setInt(1, key);
        ResultSet rs = getxml.executeQuery();
        if (rs.next()) {
            xml = rs.getString(1);
        } else {
            xml = null;
        }
        getxml.clearParameters();
        return xml;
    }

    protected boolean hasValue(Object value) {
        boolean b = false;
        try {
            String s = Serialize(value);
            containsvalue.setString(1, s);
            ResultSet rs = containsvalue.executeQuery();
            b = rs.next();
            rs.close();
            containsvalue.clearParameters();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    protected abstract void initStatement(String table) throws SQLException;

    protected void insert(final Integer key, String s) throws SQLException {
        insert.setInt(1, key);
        insert.setString(2, s);
        insert.execute();
        insert.clearParameters();
        usedKey.add(key);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Set<Integer> keySet() {
        try {
            return loadKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Set<Integer> loadKeys() throws SQLException {
        ResultSet rs = getRStocache();
        TreeSet<Integer> set = new TreeSet<Integer>();
        while (rs.next()) {
            set.add(rs.getInt(1));
        }
        return set;
    }

    void lockValues(Object key) throws IllegalAccessException {
        checkLocked(key);
        try {
            PreparedStatement pss = HSQLDBMap.connection.prepareStatement("insert into " + table + "temp values (?)");
            pss.setInt(1, key.hashCode());
            pss.execute();
            lockedID = key.hashCode();
        } catch (SQLException ex) {
            Logger.getLogger(HSQLDBMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void notifytoUpdate(final Integer key) {
        Runnable r = new Runnable() {

            public void run() {
                try {
                    synchronized (toUpdate) {
                        toUpdate.setInt(1, key);
                        toUpdate.setDouble(2, (double) System.currentTimeMillis());
                        toUpdate.execute();
                        toUpdate.clearParameters();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(HSQLDBMap.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        new Thread(r).start();
    }

    @Override
    public V put(final Integer key, final V value) {
        try {
            String s = serializer.toString(value);
            if (!containsKey(key)) {
                insert(key, s);
                //System.out.println("insert");
            } else {
                update(s, key);
                //System.out.println("update");
            }
            mapKeys.put(key, s);
            cachedValues.put(s.hashCode(), value);
        } catch (SQLException ex) {
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends V> m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void releaseLock(Integer key) {
        try {
            PreparedStatement pss = HSQLDBMap.connection.prepareStatement("delete from " + table + "temp where ID= ?");
            pss.setInt(1, key);
            pss.execute();
        } catch (SQLException ex) {
            Logger.getLogger(HSQLDBMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public V remove(Object key) {
        try {
            HSQLDBMap.connection.prepareCall("delete from " + table + " where ID=" + key.hashCode()).execute();
            notifytoUpdate(lockedID);
            usedKey.remove(key.hashCode());
            HSQLDBMap.connection.close();
            notifier.NotifyUpdate(key.hashCode(), "delete");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setNotifier(AbstractProxy<V> notifier) {
        this.notifier = notifier;
    }

    @Override
    public int size() {
        int i = getSize();
        return i;
    }

    protected void update(String s, final Integer key) throws SQLException {
        updateps.setString(1, s);
        updateps.setInt(2, key);
        updateps.executeUpdate();
        updateps.clearParameters();
    }

    public Collection<V> values() {
        long l = System.currentTimeMillis();
        cache();
        Vector<V> vec = new Vector<V>(mapKeys.size() + 120);
        for (Integer ob : mapKeys.keySet()) {
            vec.add((V) get(ob));
        }
        System.out.println("\n\nmap" + table + mapKeys.size());
        System.out.println("cache " + table + cachedValues.size());
        System.out.println("\ttime :" + (System.currentTimeMillis() - l));
        return vec;
    }

    protected void initSerializer(Information information) {
        if (information.ser == null) {
            serializer = new XMLSerializier();
        } else {
            serializer = information.ser;
        }
    }
}
