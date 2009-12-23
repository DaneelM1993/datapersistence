/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;

import persistence.Serializer.Serializer;

/**
 *
 * @author Wizard1993
 */
public class Information {

    public static Information impostazioni;

    public static Information getImpostazioni() {
        if (impostazioni == null) {
            setInformation(new Information());
        }
        return impostazioni;
    }

    /**
     * @param inf the impostazioni to set
     */
    public static void setInformation(Information inf) {
        impostazioni = inf;
    }
    private  String jdbcurl;
    private String jdbcdriver;
    private String user;
    private String psw;
    public Serializer ser;



    public Information setImpostazioni(String jdbcurl, String jdbcdriver, String user, String psw) {
        this.jdbcurl = jdbcurl;
        this.jdbcdriver = jdbcdriver;
        this.user = user;
        this.psw = psw;
        return this;
    }

    /**
     * @return the jdbcurl
     */
    public String getJdbcurl() {
        return jdbcurl;
    }

    /**
     * @param jdbcurl the jdbcurl to set
     */
    public void setJdbcurl(String jdbcurl) {
        this.jdbcurl = jdbcurl;
    }

    /**
     * @return the jdbcdriver
     */
    public String getJdbcdriver() {
        return jdbcdriver;
    }

    /**
     * @param jdbcdriver the jdbcdriver to set
     */
    public void setJdbcdriver(String jdbcdriver) {
        this.jdbcdriver = jdbcdriver;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the psw
     */
    public String getPsw() {
        return psw;
    }

    /**
     * @param psw the psw to set
     */
    public void setPsw(String psw) {
        this.psw = psw;
    }
}

