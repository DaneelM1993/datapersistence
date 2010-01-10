/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guicomponent;

import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 * @author Wizard1993
 */
public class TableBinder<T> {

    String Coloumn[];
    TreeMap<Integer,Row> rows=new TreeMap<Integer,Row>();
    public TableBinder(String[] Coloumn) {
        this.Coloumn = Coloumn;
    }

    public void addRow(T t) {
        Row row=new Row(Coloumn);
        row.setData(t);
        rows.put(t.hashCode(),row);
    }
    public void removeRow(T t){
        rows.remove(t.hashCode());
    }
}
