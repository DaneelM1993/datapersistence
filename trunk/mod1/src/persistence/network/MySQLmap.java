/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.network;

import com.thoughtworks.xstream.XStream;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import persistence.local.Information;

/**
 *
 * @author wizard1993
 */
public class MySQLmap<V> implements Map<Integer, V> {

    private PreparedStatement insert = null;
    private PreparedStatement updateps = null;
    private PreparedStatement getxml = null;
    private PreparedStatement toUpdate = null;
    private PreparedStatement getsize = null;
    private PreparedStatement containsvalue = null;
    private Integer lockedID;
    private double lastUpdate;
    private String table;
    private TreeMap<Integer, String> mapKeys = new TreeMap<Integer, String>();
    private TreeMap<Integer, V> cachedValues = new TreeMap<Integer, V>();
    private TreeSet<Integer> usedKey = new TreeSet<Integer>();
    private static Connection connection;
    private XStream xStream = new XStream();
    private boolean first = true;

    @Override
    public int size() {
        int i = getSize();
        return i;

    }

    public MySQLmap(String table, Information information) {
        this.table = table;
        try {

            Class.forName(information.getJdbcdriver());
            connection = DriverManager.getConnection(information.getJdbcurl(), information.getUser(), information.getPsw());
            createTables();

            initStatement(table);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    @Override
    public boolean isEmpty() {
        return size() == 0;

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

    @Override
    public V get(Object key) throws IllegalAccessError {
        try {
            String xml = getXML(key.hashCode());
            if (xml == null) {
                return null;
            }
            return Deserialize(xml);
        } catch (Exception ex) {
            Logger.getLogger(MySQLmap.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public V put(final Integer key, final V value) {
        try {
            String s = xStream.toXML(value);
            if (!containsKey(key)) {
                insert(key, s);
            } else {
                update(s, key);
            }
            mapKeys.put(key, s);
            cachedValues.put(s.hashCode(), value);
            notifytoUpdate(key);



        } catch (SQLException ex) {
        }

        return null;

    }

    @Override
    public V remove(Object key) {
        try {
            connection.prepareCall("delete from " + table + " where ID=" + key.hashCode()).execute();
            notifytoUpdate(lockedID);
            usedKey.remove(key.hashCode());
            mapKeys.remove(key);


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
            connection.prepareCall("delete  from " + table).execute();
            mapKeys.clear();
            cachedValues.clear();
            first = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    /**
     * Due perfomance reason this method is deprecated, it cause le loading of all the element of database blocking application maybe for a long time
     * consider tu use keyset in addiction with get methods
     *
     */
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
        try {
            if (first) {
                ResultSet rs = connection.createStatement().executeQuery("select * from " + table + ";");
                while (rs.next()) {
                    mapKeys.put(rs.getInt(1), rs.getString(2));
                }
                rs.close();
                first = false;
            } else {
                ResultSet rs = getRStocache();
                while (rs.next()) {
                    Integer i = rs.getInt(1);
                    String str = getfromQuery(i);
                    if (str != null) {
                        mapKeys.put(i, str);
                    }
                }
            }

            lastUpdate = System.currentTimeMillis();
        } catch (Exception e) {
        }

        return 0;
    }

    @Override
    protected void finalize() throws Throwable {
        connection.close();
        super.finalize();
    }

    void checkLocked(Object key) throws IllegalAccessException {
        try {
            ResultSet isLocked = connection.createStatement().executeQuery("select * from " + table + "temp where ID=" + key.hashCode());
            if (isLocked.next()) {
                throw new IllegalAccessException("the object is locked");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLmap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void lockValues(Object key) throws IllegalAccessException {
        checkLocked(key);
        try {
            PreparedStatement pss = connection.prepareStatement("insert into " + table + "temp values (?)");
            pss.setInt(1, key.hashCode());
            pss.execute();
            lockedID = key.hashCode();
        } catch (SQLException ex) {
            Logger.getLogger(MySQLmap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void releaseLock(Integer key) {
        try {
            PreparedStatement pss = connection.prepareStatement("delete from " + table + "temp where ID= ?");
            pss.setInt(1, key);
            pss.execute();
        } catch (SQLException ex) {
            Logger.getLogger(MySQLmap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createTables() {
        try {
            Statement statement = connection.createStatement();
            try {
                statement.executeUpdate("create table toupdate" + table + " (ID integer,Time double) ENGINE=MEMORY;");
            } catch (Exception e) {
                System.err.println("e1" + table);
            }
            try {
                statement.executeUpdate("create table " + table + "temp (ID integer, Primary key(ID))ENGINE=MEMORY;");
            } catch (Exception e) {
                System.err.println("e2" + table);
            }
            try {
                statement.executeUpdate("create table " + table + " (ID integer,XML text, Primary key(ID));");
            } catch (Exception e) {
                System.err.println("e3" + table);
            }

            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(lockedID);
        }
    }

    private ResultSet getRStocache() throws SQLException {
        ResultSet rs = null;
        if (first) {
            rs = connection.prepareCall("select (ID) from " + table + ";").executeQuery();
        } else {
            rs = connection.prepareCall("select (ID) from toupdate" + table + " where Time>" + lastUpdate).executeQuery();
        }

        return rs;
    }

    private String getfromQuery(final Integer key) throws SQLException {
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

    private void initStatement(String table) throws SQLException {
        insert = connection.prepareStatement("insert into " + table + " values(?,?)");
        updateps = connection.prepareStatement("update " + table + " set XML=? where ID=?");
        toUpdate = connection.prepareStatement("insert into toupdate" + table + " values (?,?);");
        getxml = connection.prepareStatement("select XML from " + table + " where ID = ?");
        getsize = connection.prepareStatement("select Count(*) from " + table);
        containsvalue = connection.prepareStatement("select Count(*) from " + table + " where XML = ?");
    }

    private void insert(final Integer key, String s) throws SQLException {
        insert.setInt(1, key);
        insert.setString(2, s);
        insert.execute();
        insert.clearParameters();
        usedKey.add(key);
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
                    Logger.getLogger(MySQLmap.class.getName()).log(Level.SEVERE, null, ex);
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

    private void update(String s, final Integer key) throws SQLException {
        updateps.setString(1, s);
        updateps.setInt(2, key);
        updateps.executeUpdate();
        updateps.clearParameters();
    }

    private int getSize() {
        int num = 0;
        try {

            ResultSet rs = getsize.executeQuery();

            if (rs.next()) {
                num = rs.getInt(1);
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(121);
        }
        return num;
    }

    private boolean hasValue(Object value) {
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

    private String Serialize(Object value) {
        if (mapKeys.containsKey(value.hashCode())) {
            return mapKeys.get(value.hashCode());
        } else {
            return xStream.toXML(value);
        }
    }

    private Set<Integer> loadKeys() throws SQLException {
        ResultSet rs = getRStocache();
        TreeSet<Integer> set = new TreeSet<Integer>();
        while (rs.next()) {
            set.add(rs.getInt(1));
        }
        return set;
    }

    void close() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}