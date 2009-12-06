/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.Serializable;
import persistence.local.AbstractProxy;
import persistence.local.PersistenceList;
import persistence.local.XmlProxy;

/**
 *
 * @author Wizard1993
 */
public class City implements Serializable{
    String name;
    static AbstractProxy<Person> proxy=new XmlProxy<Person>("person.xml");
    PersistenceList<Person> people=new PersistenceList<Person>(proxy);

    public City(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public PersistenceList<Person> getPeople() {
        return people;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPeople(PersistenceList<Person> people) {
        this.people = people;
    }
    

}
