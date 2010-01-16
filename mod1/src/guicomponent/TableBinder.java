/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guicomponent;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 *
 * @author Wizard1993
 */
public class TableBinder<T> implements  TableModel{
    ArrayList<Row> rows=new ArrayList<Row>();
    DataPool dp;
    String column[];
    String Clazz;
    CopyOnWriteArraySet<TableModelListener> listner=new CopyOnWriteArraySet<TableModelListener>();
    
    public TableBinder(String[] column,String clazz,T[] rows) {
        this.column=column;
        Clazz=clazz;
        createRows(rows);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
       return column.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            Field f = rows.get(rowIndex).getData().get(columnIndex);
            return f.get(rows.get(rowIndex).o);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(TableBinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(TableBinder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
        rows.get(rowIndex).setValue(columnIndex,aValue);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listner.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listner.remove(l);
    }


}
