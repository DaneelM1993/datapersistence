/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guicomponent;

import java.util.TreeMap;
import javax.swing.table.DefaultTableModel;
import persistence.local.AbstractProxy;

/**
 *
 * @author Wizard1993
 */
public class TableBinder<T> {
    AbstractProxy<T> proxy;
    String Coloumn[];
    TreeMap<Integer,Row> rows=new TreeMap<Integer,Row>();

    public TableBinder(AbstractProxy<T> proxy, String[] Coloumn) {
        this.proxy = proxy;
        this.Coloumn = Coloumn;
    }
    

    private void addRow(T t) {
        Row row=new Row(Coloumn);
        row.setData(t);
        rows.put(t.hashCode(),row);
    }
    private void removeRow(T t){
        rows.remove(t.hashCode());
    }
    public DefaultTableModel render(){
        DefaultTableModel dtm=new DefaultTableModel(Coloumn, rows.size());
        for (Integer integer : rows.keySet()) {
            dtm.addColumn(rows.get(integer).getData());
        }
        return dtm;
    }
}
