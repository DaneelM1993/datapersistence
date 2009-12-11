/*
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.Serializable;
import java.util.Random;
import java.util.Vector;
import persistence.local.AbstractProxy;
import persistence.local.Information;
import persistence.network.HSQLDBProxy;
import persistence.network.SQLiteProxy;

/**
 *
 * @author Wizard1993
 */
public class Main {

    static Vector<Person> vec;

    public static void main(String... args) throws Exception {
        Random random=new Random(12);
        AbstractProxy<Person> personProxy =new HSQLDBProxy<Person>("person", new Information().setImpostazioni("jdbc:hsqldb:file:D:\\test1.db;shutdown=true", "org.hsqldb.jdbcDriver", "sa", ""));
        moretest(personProxy, random);
        //Thread.sleep(150000);
    }

    private static void testone(AbstractProxy<Person> personProxy) {
        Person p = new Person("asd", "qw", 3);
        personProxy.map.put(p.hashCode(), p);
        personProxy.map.values();
        personProxy.dispose();
    }

    private static void moretest(AbstractProxy<Person> personProxy, Random random) {
        String[] names = {"mario", "gianni", "paolo", "giovanni", "antonio", "carlo","pippo","sergio"};
        String[] surnames = {"rossi", "verdi", "bianchi", "ciompi","neri","gialli","visconti","sforza","pazzi"};
        personProxy.map.clear();
        System.out.println(personProxy.map.values());
        for (int i = 0; i < 150; i++) {
            Integer idx = Math.abs(random.nextInt());
            Person p = new Person(surnames[idx % surnames.length], names[idx % names.length], idx%100);
            personProxy.map.put(personProxy.generateKey(), p);
        }
        personProxy.map.values();
        personProxy.dispose();
    }

    
}
