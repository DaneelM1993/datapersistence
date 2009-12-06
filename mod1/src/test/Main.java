/*
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Vector;
import persistence.local.AbstractProxy;
import persistence.local.Information;
import persistence.network.SQLiteProxy;

/**
 *
 * @author Wizard1993
 */
public class Main {

    static Vector<Person> vec;

    public static void main(String... args) throws Exception {
        Information information=new Information().setImpostazioni("jdbc:sqlite:D:\\database.db", "org.sqlite.JDBC", "", "");
        AbstractProxy<Person> ap=new SQLiteProxy<Person>("person", information);
        Person p=new Person("mario", "rossi", 35.0f);
        ap.map.put(ap.generateKey(), p);
        System.out.println(ap.map.values());
        ap.dispose();
    }

    
}
