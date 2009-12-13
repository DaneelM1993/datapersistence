/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence.network;

import java.io.Serializable;

/**
 *
 * @author Wizard1993
 */
public interface Serializer {
    String toString(Serializable t);
    Serializable fromString(String str);
}
