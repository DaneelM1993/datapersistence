/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.network;

import persistence.local.Information;
import com.thoughtworks.xstream.XStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 *
 * @author wizard1993
 */
class SQLmap<V> implements Map<Integer, V> {

    private PreparedStatement insert = null;
    private PreparedStatement updateps = null;
    private Integer lockedID;
    private double lastUpdate;
    private String table;
    private PreparedStatement getxml;
    private HashMap<Integer, String> mapKeys = new HashMap<Integer, String>(1024, 0.65f);
    private HashMap<Integer, V> cachedValues = new HashMap<Integer, V>(4096);
    private Set<Integer> usedKey = new TreeSet<Integer>();
    Connection con;
    XStream xStream = new XStream();
    PreparedStatement toUpdate = null;
    private boolean first = true;

    @Override
    public int size() {
        return usedKey.size();

    }

    public SQLmap(String table, Information information) {
        this.table = table;
        try {
            Class.forName(information.getJdbcdriver());
            con = DriverManager.getConnection(information.getJdbcurl(), information.getUser(), information.getPsw());
            createTables();
            initStatement(table);
            cache();
            initKeys();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    @Override
    public boolean isEmpty() {
        cache();
        return mapKeys.isEmpty();

    }

    @Override
    public boolean containsKey(Object key) {
        return usedKey.contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        cache();
        return mapKeys.containsKey(value);
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
            Logger.getLogger(SQLmap.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public V put(final Integer key, final V value) {
        try {
            notifytoUpdate(key);
            String s = xStream.toXML(value);
            if (!containsKey(key)) {                
                insert.setInt(1, key);
                insert.setString(2, s);
                insert.execute();
                insert.clearParameters();
                usedKey.add(key);
            } else {
                System.out.println("update");
                updateps.setString(1, s);
                updateps.setInt(2, key);
                updateps.executeUpdate();
                updateps.clearParameters();
            }
            mapKeys.put(key, s);

        } catch (SQLException ex) {
//            Logger.getLogger(SQLmap.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    @Override
    public V remove(Object key) {
        try {
            con.prepareCall("delete from " + table + " where ID=" + key.hashCode()).execute();
            notifytoUpdate(lockedID);
            usedKey.remove(key.hashCode());
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends V> m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        try {
            con.prepareCall("delete  from " + table).execute();
            mapKeys.clear();
            first = true;
            initKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<Integer> keySet() {
        return keySet();
    }

    @Override
    public Collection<V> values() {
        long l = System.currentTimeMillis();
        cache();
        Vector<V> vec = new Vector<V>(usedKey.size()+120);
        for (Integer ob : mapKeys.keySet()) {
            vec.add((V) get(ob));
        }
        System.out.println("\n\nmap" + table + mapKeys.size());
        System.out.println("cache " + table + cachedValues.size());
        System.out.println("\ttime :" + (System.currentTimeMillis() - l));
        return vec;
    }

    @Override
    public Set<Entry<Integer, V>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private V Deserialize(String xml) {
        if (cachedValues.containsKey(xml.hashCode())) {
            return (V) cachedValues.get(xml.hashCode());
        } else {
            V v = (V) xStream.fromXML(xml);
            cachedValues.put(xml.hashCode(), v);
            return v;
        }
    }

    private Integer cache() {
        int i = 0;
        try {
            ResultSet rs = getRStocache();
            while (rs.next()) {
                Integer key = rs.getInt(1);
                mapKeys.put(key, getXML(key));
                i++;
            }
            rs.close();
            con.prepareCall("delete from toupdate" + table).execute();
            lastUpdate = System.currentTimeMillis();
            System.out.println(i);

        } catch (SQLException ex) {
            Logger.getLogger(SQLmap.class.getName()).log(Level.SEVERE, null, ex);
        }
        return i;
    }

    @Override
    protected void finalize() throws Throwable {
        con.close();
        super.finalize();
    }

    void checkLocked(Object key) throws IllegalAccessException {
        try {
            ResultSet isLocked = con.createStatement().executeQuery("select * from used" + table + " where ID=" + key.hashCode());
            if (isLocked.next()) {
                throw new IllegalAccessException("the object is locked");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLmap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void lockValues(Object key) throws IllegalAccessException {
        checkLocked(key);
        try {
            PreparedStatement pss = con.prepareStatement("insert into used" + table + " values (?)");
            pss.setInt(1, key.hashCode());
            pss.execute();
            lockedID = key.hashCode();
        } catch (SQLException ex) {
            Logger.getLogger(SQLmap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void releaseLock(Integer key) {
        try {
            PreparedStatement pss = con.prepareStatement("delete from used" + table + " where ID= ?");
            pss.setInt(1, key);
            pss.execute();
        } catch (SQLException ex) {
            Logger.getLogger(SQLmap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createTables() {
        try {
            Statement statement = con.createStatement();
            statement.executeUpdate("create table toupdate" + table + " (ID integer,Time double)");
            statement.executeUpdate("create table " + table + "temp (ID integer);");
            statement.executeUpdate("create table " + table + " (ID integer,XML text);");
            statement.close();

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private ResultSet getRStocache() throws SQLException {
        ResultSet rs = null;
        if (!first) {
            rs = con.prepareCall("select * from toupdate" + table + " where Time>" + lastUpdate).executeQuery();

        } else {
            rs = con.prepareCall("select (ID) from " + table + " ;").executeQuery();
            first = false;

        }
        return rs;
    }

    private String getfromQuery(Integer key) throws SQLException {
        String xml;
        getxml.setInt(1, key);
        ResultSet rs = getxml.executeQuery();
        rs.next();
        xml = rs.getString(1);
        getxml.clearParameters();
        return xml;
    }

    private void initStatement(String table) throws SQLException {
        insert = con.prepareStatement("insert into " + table + " values(?,?)");
        updateps = con.prepareStatement("update " + table + " set XML=? where ID=?");
        toUpdate = con.prepareStatement("insert into toupdate" + table + " values (?,?);");
        getxml = con.prepareStatement("select XML from " + table + " where ID = ?");
    }

    private void notifytoUpdate(final Integer key) {
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
                    Logger.getLogger(SQLmap.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        new Thread(r).start();
    }

    private String getXML(Integer key) {
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

    private void initKeys() {
        cache();
        usedKey.clear();
        this.usedKey.addAll(mapKeys.keySet());
    }
}
