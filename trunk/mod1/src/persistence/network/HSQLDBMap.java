/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.network;

import java.io.Serializable;
import java.sql.*;
import java.util.*;
import persistence.local.Information;

/**
 *
 * @author wizard1993
 */
public class HSQLDBMap<V extends Serializable> extends AbstractMap<V> implements Map<Integer, V> {

    public HSQLDBMap(String table, Information information) {
        this.table = table;
        try {
            initSerializer(information);
            if (connection == null) {
                Class.forName(information.getJdbcdriver());
                connection = DriverManager.getConnection(information.getJdbcurl(), information.getUser(), information.getPsw());

            }
            createTables();
            initStatement(this.table);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }



    @Override
    protected void initStatement(String table) throws SQLException {
        insert = connection.prepareStatement("insert into " + table + " values(?,?)");
        updateps = connection.prepareStatement("update " + table + " set XML=? where ID=?");
        toUpdate = connection.prepareStatement("insert into toupdate" + table + " values (?,?);");
        getxml = connection.prepareStatement("select XML from " + table + " where ID = ?");
        getsize = connection.prepareStatement("select Count(*) from " + table);
        containsvalue = connection.prepareStatement("select Count(*) from " + table + " where XML = ?");
    }

    @Override
    protected void createTables() {
        try {
            Statement statement = HSQLDBMap.connection.createStatement();
            try {
                statement.executeUpdate("create temp table toupdate" + table + " (ID integer,Time double)");
            } catch (Exception e) {
                System.err.println("e1" + table);
            }
            try {
                statement.executeUpdate("create temp table " + table + "temp (ID integer, Primary key(ID));");
            } catch (Exception e) {
                System.err.println("e2" + table);
            }
            try {
                statement.executeUpdate("create table " + table + " (ID integer,XML varchar, Primary key(ID));");
            } catch (Exception e) {
                System.err.println("e3" + table);
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(lockedID);
        }
    }

}
