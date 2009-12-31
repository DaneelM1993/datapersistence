/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.Serializable;

/**
 *
 * @author Wizard1993
 */

public class Person implements Serializable {
    public String cog;
    public String name;
    public Integer ages;

    public Integer getAges() {
        return ages;
    }

    public void setCog(String cog) {
        this.cog = cog;
    }


    public Person(String cog, String name, Integer ages) {
        this.cog = cog;
        this.name = name;
        this.ages = ages;
    }

    public void setAges(Integer ages) {
        this.ages = ages;
    }

    public Person() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return cog;
    }

    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.cog = surname;
    }

    @Override
    public String toString() {
        return String.format("%S-%S-%d", name,cog,ages);
    }

  
}
