/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.network;

import persistence.Serializer.XMLSerializier;
import java.io.Serializable;
import java.sql.*;
import persistence.local.Information;

/**
 *
 * @author wizard1993
 */
public class Derbymap<V extends Serializable> extends AbstractMap<V> {

    public Derbymap(String table, Information information) {
        this.table = table;
        try {
            initSerializer(information);
            if (connection == null) {
                Class.forName(information.getJdbcdriver());
                if (information.getUser().equals("")) {
                    connection = DriverManager.getConnection(information.getJdbcurl());
                } else {
                    connection = DriverManager.getConnection(information.getJdbcurl(), information.getUser(), information.getPsw());
                }

            }
            createTables();
            initStatement(table);

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
                statement.executeUpdate("create table \"toupdate" + table + "\" (ID integer,Time double)");
            } catch (Exception e) {
                System.err.println("e1" + table);
            }
            try {
                statement.executeUpdate("create table \"" + table + "temp\" (ID integer, Primary key(ID))");
            } catch (Exception e) {
                System.err.println("e2" + table);
            }
            try {
                statement.executeUpdate("create table \"" + table + "\" (ID int primary key, Data varchar(2048))");
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
    protected  void initStatement(String table) throws SQLException {
        System.out.println(table);
        insert = connection.prepareStatement("insert into \"" + table + "\" values(?,?)");
        updateps = connection.prepareStatement("update \"" + table + "\" set Data=? where ID=?");
        toUpdate = connection.prepareStatement("insert into \"toupdate" + table + "\" values (?,?)");
        getxml = connection.prepareStatement("select Data from \"" + table + "\" where ID = ?");
        getsize = connection.prepareStatement("select Count(*) from \"" + table + "\"");
        containsvalue = connection.prepareStatement("select Count(*) from \"" + table + "\" where Data = ?");
    }

   
}
