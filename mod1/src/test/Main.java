/*
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;
import persistence.Serializer.XStreamSerializer;
import persistence.local.AbstractProxy;
import persistence.local.Information;
import persistence.local.XmlProxy;
import persistence.network.*;

/**
 *
 * @author Wizard1993
 */
public class Main {

    static Vector<Person> vec;

    public static void main(String... args) throws Exception {
        Random random = new Random(123);
        Information inf = new Information().setImpostazioni("jdbc:derby:teste;create=true;", "org.apache.derby.jdbc.EmbeddedDriver", "test", "test");
        inf.ser = new XStreamSerializer();
        Information inf1 = new Information().setImpostazioni("jdbc:hsqldb:prova32.db;shutdown=true", "org.hsqldb.jdbcDriver", "sa", "");
        inf1.ser = new XStreamSerializer();
        AbstractProxy<Person> personProxy = new XmlProxy<Person>("D:\\out.xml");
        AbstractProxy<Person> p1 = new HSQLDBProxy<Person>("person", inf1);
        //moretest(personProxy, random);
        compare(p1, personProxy, random);
        personProxy.commit();
        testunit(p1, personProxy);

    }

    private static void testone(AbstractProxy<Person> personProxy) {
        Person p = new Person("asd", "qw", 3);
        personProxy.map.put(p.hashCode(), p);
        personProxy.map.values();
        personProxy.dispose();
    }

    private static void moretest(AbstractProxy<Person> personProxy, Random random) {
        String[] names = {"mario", "gianni", "paolo", "giovanni", "antonio", "carlo", "pippo", "sergio", "luigi", "adelaide", "carmelo", "gennaro"};
        String[] surnames = {"rossi", "verdi", "bianchi", "ciompi", "neri", "colonna", "gialli", "visconti", "sforza", "pazzi", "uberti", "bonaparte", "napolitano", "mazzini", "cavour", "bertinotti"};
        //personProxy.map.clear();
        System.out.println(personProxy.map.values());
        for (int i = 0; i < 10; i++) {
            Integer idx = random.nextInt(100);
            Person p = new Person(surnames[idx % surnames.length], names[idx % names.length], idx);
            personProxy.map.put(personProxy.generateKey(), p);
        }
        System.out.println(personProxy.map.values());
        personProxy.dispose();

    }

    private static void compare(AbstractProxy<Person> person0, AbstractProxy<Person> person1, Random random) {
        String[] names = {"mario", "gianni", "paolo", "giovanni", "antonio", "carlo", "pippo", "sergio", "luigi", "adelaide", "carmelo", "gennaro"};
        String[] surnames = {"rossi", "verdi", "bianchi", "ciompi", "neri", "colonna", "gialli", "visconti", "sforza", "pazzi", "uberti", "bonaparte", "napolitano", "mazzini", "cavour", "bertinotti"};
        person0.map.clear();
        person1.map.clear();
        for (int i = 0; i < 10; i++) {
            Integer idx = random.nextInt(100);
            Person p = new Person(surnames[idx % surnames.length], names[idx % names.length], idx);
            person0.map.put(person0.generateKey(), p);
            person1.map.put(person1.generateKey(), p);
        }
    }

    static void testunit(AbstractProxy<Person> a, AbstractProxy<Person> good) {
        Random r = new Random();
        compare(a, good, r);
        Vector<Person> p=new Vector<Person>(a.map.values());
        Vector<Person> p1=new Vector<Person>(good.map.values());
        Collections.sort(p);
        Collections.sort(p1);
        System.out.println(Arrays.equals(p.toArray(), p1.toArray()));

    }
}
