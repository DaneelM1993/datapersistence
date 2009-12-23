/*
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import persistence.Serializer.JSONSerializer;
import java.io.Serializable;
import java.util.Random;
import java.util.Vector;
import persistence.Serializer.CastorSerializer;
import persistence.Serializer.XStreamSerializer;
import persistence.local.AbstractProxy;
import persistence.local.Information;
import persistence.network.*;

/**
 *
 * @author Wizard1993
 */
public class Main {

    static Vector<Person> vec;

    public static void main(String... args) throws Exception {
        Random random=new Random(12);
        Information inf=new Information().setImpostazioni("jdbc:hsqldb:hsql://localhost/prova32;shutdown=true", "org.hsqldb.jdbcDriver", "sa", "");
        Information inf1=new Information().setImpostazioni("jdbc:hsqldb:mem:prova32", "org.hsqldb.jdbcDriver", "sa", "");
        inf1.ser=new CastorSerializer();
        AbstractProxy<Person> personProxy =new HSQLDBProxy<Person>("person", inf1);
        moretest(personProxy, random);
        //Thread.sleep(4546464);
        //personProxy.dispose();
        
    }

    private static void testone(AbstractProxy<Person> personProxy) {
        Person p = new Person("asd", "qw", 3);
        personProxy.map.put(p.hashCode(), p);
        personProxy.map.values();
        personProxy.dispose();
    }

    private static void moretest(AbstractProxy<Person> personProxy, Random random) {
        String[] names = {"mario", "gianni", "paolo", "giovanni", "antonio", "carlo","pippo","sergio","luigi","adelaide","carmelo","gennaro"};
        String[] surnames = {"rossi", "verdi", "bianchi", "ciompi","neri","gialli","visconti","sforza","pazzi","uberti","bonaparte","napolitano","mazzini","cavour","bertinotti"};
        personProxy.map.clear();
        System.out.println(personProxy.map.values());
        for (int i = 0; i <100; i++) {
            Integer idx = random.nextInt(100);
            Person p = new Person(surnames[idx % surnames.length], names[idx % names.length], idx);
            personProxy.map.put(personProxy.generateKey(), p);

        }
        System.out.println(personProxy.map.values());
       // personProxy.dispose();

    }

    
}
