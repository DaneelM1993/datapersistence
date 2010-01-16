/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.network;

import java.io.Serializable;
import java.sql.*;
import persistence.local.Information;

/**
 *
 * @author wizard1993
 */
public class MySQLmap<V extends Serializable> extends AbstractMap<V> {

    public MySQLmap(String table, Information information) {
        this.table = table;
        try {

            Class.forName(information.getJdbcdriver());
            connection = DriverManager.getConnection(information.getJdbcurl(), information.getUser(), information.getPsw());
            createTables();
//            initStatement(table);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    @Override
    protected  void createTables() {
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

    @Override
    protected void initStatement(String table) throws SQLException {
        insert = connection.prepareStatement("insert into " + table + " values(?,?)");
        updateps = connection.prepareStatement("update " + table + " set XML=? where ID=?");
        toUpdate = connection.prepareStatement("insert into toupdate" + table + " values (?,?);");
        getxml = connection.prepareStatement("select XML from " + table + " where ID = ?");
        getsize = connection.prepareStatement("select Count(*) from " + table);
        containsvalue = connection.prepareStatement("select Count(*) from " + table + " where XML = ?");
    }

}
