/*
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Random;
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
        Random random=new Random();
        AbstractProxy<Person> personProxy =new SQLiteProxy<Person>("person", new Information().setImpostazioni("jdbc:sqlite:D:\\database.db", "org.sqlite.JDBC", "", ""));
        String names[]={"mario","gianni","paolo","giovanni","antonio","carlo"};
        String surnames[]={"rossi","verdi","bianchi","ciompi"};
        personProxy.map.clear();
        for(int i=0;i<150;i++){
            Integer idx=random.nextInt(100);
            Person p=new Person(surnames[idx%surnames.length], names[idx%names.length], idx);
            personProxy.map.put(p.hashCode(), p);
        }
        personProxy.dispose();
    }

    
}
