/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import persistence.local.AbstractProxy;
import persistence.local.Information;
import persistence.network.HSQLDBProxy;

/**
 *
 * @author Wizard1993
 */
public class Main1 {
    public static void main(String... args){
        Information.impostazioni=new Information().setImpostazioni("jdbc:hsqldb:hsql://localhost/person", "org.hsqldb.jdbcDriver", "sa", "");
        AbstractProxy<Person> ap=new HSQLDBProxy<Person>("test", Information.impostazioni);
        

    }
}
