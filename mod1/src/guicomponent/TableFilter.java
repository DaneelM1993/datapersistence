/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guicomponent;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import persistence.local.AbstractProxy;
import persistence.local.Provider;
import persistence.local.UpdateEvent;
import persistence.local.updateEventListener;

/**
 *
 * @author Wizard1993
 */
public class TableFilter<T> extends Filter<T> implements updateEventListener {

    private List<T> processed;
    JTable generatedTable;
    final AbstractProxy<T> absp;

    public TableFilter(String s, AbstractProxy<T> p, String[] field) {
        super(s);
        if (field != null) {
            Field = Arrays.asList(field);
        }
        absp = p;
        absp.AddComponentToNotify(this);
    }

    public List<T> filter(List<T> source) {
        ArrayList<T> swap = new ArrayList<T>();
        for (int idx = 0; idx < conditions.size(); idx++) {
            swap.clear();
            for (T t : source) {
                if (conditions.get(idx).sotisfie(t)) {
                    swap.add(t);
                }
            }
            source = swap;
        }
        for (int i = 0; i < operators.size(); i++) {
            source = operators.get(i).doSome(source);
        }
        return source;
    }

    public JTable createTable() {
        try {
            DefaultTableModel dtm = generateModel();
            if (generatedTable != null) {
                generatedTable.setModel(dtm);
            } else {
                generatedTable = new JTable(dtm);
            }
            return generatedTable;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private DefaultTableModel generateModel() throws Exception {
        List<String> fields = loadFields();
        loadMethods();

        processed = filter(absp.getDataList());
        Object[][] o = createTableData(fields);
        DefaultTableModel dtf = new DefaultTableModel(o, fields.toArray());
        return dtf;
    }

    private Object[][] createTableData(List<String> fields) throws Exception {
        Object[][] o = new Object[processed.size()][fields.size()];
        for (int i = 0; i < processed.size(); i++) {
            for (int j = 0; j < fields.size(); j++) {
                System.out.println(fields.get(j));
                Object ob = getValue(fields.get(j), processed.get(i));
                if (fieldWrapper.containsKey(fields.get(j))) {
                    o[i][j] = fieldWrapper.get(fields.get(j)).toString(ob);
                } else {
                    o[i][j] = ob;
                }
            }
        }
        return o;
    }

    public void UpdateEventPerformed(UpdateEvent ev) {
        createTable();

    }

    public T returnElement(int i) {
        return processed.get(i);
    }

    protected T setValues(int i,T target) throws Exception{
        for (int j = 0; j < Field.size(); j++) {
            String string = Field.get(j);
            setField(string, target, generatedTable.getValueAt(i, j).toString());
        }

        return null;
    }

    @Override
    protected T save() {
        try {
            for (int i = 0; i < processed.size(); i++) {
                T t = processed.get(i);
                setValues(i, t);
                absp.map.put(t.hashCode(), t);
            }
            absp.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

   
}
