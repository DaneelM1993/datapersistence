/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guicomponent;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import persistence.local.AbstractProxy;

/**
 *
 * @author Wizard1993
 */
public class TableBinder<T> implements  TableModel{
    ArrayList<Row> rows=new ArrayList<Row>();
    DataPool dp;
    String column[];
    String Clazz;

    public TableBinder(String[] column,String clazz,T[] rows) {
        this.column=column;
        Clazz=clazz;
        createRows(rows);
     
    }

    @Override
    public int getRowCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getColumnCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    private void createRows(T[] rows){
        for (T t : rows) {
            Row r=new Row(column);
            r.setData(t);
            this.rows.add(r);
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        return column[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
       String string=column[columnIndex];
        try {
            return Class.forName(Clazz).getField(string).getType();
        } catch (Exception ex) {
            Logger.getLogger(TableBinder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
