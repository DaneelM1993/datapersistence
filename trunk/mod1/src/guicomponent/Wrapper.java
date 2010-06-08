/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guicomponent;

/**
 *
 * @author wizard1993
 */
public interface  Wrapper<T> {
    String toString(T t);
    T fromString(String s);
}
