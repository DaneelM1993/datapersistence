/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.network;

import persistence.local.Information;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.PrintStream;
import java.sql.*;
import java.util.*;
import java.util.logging.*;



/**
 *
 * @author wizard1993
 */
public class NetworkMap1<V> implements Map<Integer, V> {

    private Integer lockedID;
    private String table;
    boolean update = true;
    private HashMap<Integer, String> mapKeys = new HashMap<Integer, String>(1024, 0.65f);
    private HashMap<Integer, V> cacheValues = new HashMap<Integer, V>(4096);
    String ip;
    Connection con;
    XStream xStream = new XStream();
    PrintStream logStrem = new PrintStream(System.out);

    public void setLogStrem(PrintStream logStrem) {
        this.logStrem = logStrem;
    }

    public PrintStream getLogStrem() {
        return logStrem;
    }

    @Override
    public int size() {
        cache();
        return mapKeys.size();
    }

    public NetworkMap1(String table,Connection con) {
        
        try {
            this.table=table;
            this.con=con;
        } catch (Exception e) {
            //e.printStackTrace();
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
        cache();
        return mapKeys.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        cache();
        return mapKeys.containsKey(value);
    }

    @Override
    public V get(Object key) throws IllegalAccessError {
        try {
            String xml;

            if (mapKeys.containsKey(key.hashCode())) {
                xml = (String) mapKeys.get(key.hashCode());
            } else {
                ResultSet rs = con.prepareStatement("select * from " + table + "where ID =" + key.hashCode()).executeQuery();
                try {
                    xml = rs.getString(2);
                } catch (Exception e) {
                    if (rs.next()) {
                        xml = rs.getString(2);
                    } else {
                        return null;
                    }
                }
            }

            return Deserialize(xml);
        } catch (Exception ex) {
            Logger.getLogger(NetworkMap1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public V put(Integer key, V value) {
        try {
            PreparedStatement ps = null;


            if (!containsKey(key)) {
                ps = con.prepareStatement("insert into " + table + " values(?,?)");
                ps.setInt(1, key);
                System.out.println("insert" + key);
                ps.setString(2, xStream.toXML(value));
                ps.execute();

            } else {
                System.out.println("update");
                ps = con.prepareStatement("update " + table + " set XML=? where ID=?");

                ps.setString(1, xStream.toXML(value));
                ps.setInt(2, key);
                ps.executeUpdate();
            }
            update = true;

            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(NetworkMap1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        try {

            con.prepareCall("delete from " + table + " where ID=" + key.hashCode()).execute();
            update = true;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<Integer> keySet() {
        cache();
        return mapKeys.keySet();
    }

    @Override
    public Collection<V> values() {
        long l = System.currentTimeMillis();
        cache();
        Vector<V> vec = new Vector<V>(mapKeys.size());
        for (Integer ob : mapKeys.keySet()) {
            vec.add((V) get(ob));
        }
        System.out.println("\n\nmap" + table + mapKeys.size());
        System.out.println("cache " + table + cacheValues.size());
        System.out.println("\ttime :" + (System.currentTimeMillis() - l));
        return vec;
    }

    @Override
    public Set<Entry<Integer, V>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private V Deserialize(String xml) {
        if (cacheValues.containsKey(xml.hashCode())) {
            return (V) cacheValues.get(xml.hashCode());
        } else {
            V v = (V) xStream.fromXML(xml);
            cacheValues.put(xml.hashCode(), v);
            return v;
        }
    }

    private void cache() {
        try {
            if (update) {
                ResultSet rs = con.prepareStatement("select * from " + table).executeQuery();
                while (rs.next()) {
                    mapKeys.put(rs.getInt(1), rs.getString(2));
                }
                update = false;
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(NetworkMap1.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            Logger.getLogger(NetworkMap1.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(NetworkMap1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void releaseLock(Integer key ) {
        try {
            PreparedStatement pss = con.prepareStatement("delete from used" + table + " where ID= ?");
            pss.setInt(1, key);
            pss.execute();
        } catch (SQLException ex) {
            Logger.getLogger(NetworkMap1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
