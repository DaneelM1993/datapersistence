/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.Serializable;
import persistence.local.AbstractProxy;
import persistence.local.PersistenceSet;
import persistence.local.XmlProxy;

/**
 *
 * @author Wizard1993
 */
public class City implements Serializable{
    String name;
    static AbstractProxy<Person> proxy=new XmlProxy<Person>("person.xml");
    PersistenceSet<Person> people=new PersistenceSet<Person>(proxy);

    public City(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public PersistenceSet<Person> getPeople() {
        return people;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPeople(PersistenceSet<Person> people) {
        this.people = people;
    }
    

}
