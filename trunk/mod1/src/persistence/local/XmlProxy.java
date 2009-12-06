/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;


import com.thoughtworks.xstream.XStream;
import java.io.*;
import java.util.*;
import javax.swing.SwingUtilities;

/**
 *
 * @author wizard1993
 */
public class XmlProxy<T extends Serializable> extends  AbstractProxy<T> {

    private Set<updateEventListener> set = Collections.synchronizedSet(new HashSet<updateEventListener>());    
    private XStream xstream = new XStream();
    private String path;
    boolean toUpdate;

    public XmlProxy() {
        map=new TreeMap<Integer, T>();
    }

    @Override
    public void AddComponentToNotify(updateEventListener uel) {
        set.add(uel);
    }

    public XmlProxy(String path) {
        this.path = path;
        LoadValues();
        System.out.println("init XML Proxy"+path);

    }

    @Override
    public void LoadValues() {
        try {
            BufferedInputStream bis=new BufferedInputStream(new FileInputStream(path));
            map=(TreeMap<Integer, T>)xstream.fromXML(bis);
            bis.close();
        }
        catch(FileNotFoundException fnfe){
            //nothing
            map=new TreeMap<Integer, T>();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        catch (Exception e) {
            map=new TreeMap<Integer, T>();
        }
    }

    @Override
    public void NotifyUpdate() {
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (updateEventListener eventListener : set) {
                    eventListener.UpdateEventPerformed();
                }
            }
        });
    }

    @Override
    public void RemoveComponentToNotify(updateEventListener uel) {
        set.remove(uel);
    }

    @Override
    public void commit() {
        try {
            File f=new File(path);
            f.createNewFile();
            xstream.toXML(map,new FileOutputStream(f));
            NotifyUpdate();
        } catch (FileNotFoundException fnfex) {
            fnfex.printStackTrace();
        } catch (com.thoughtworks.xstream.converters.ConversionException ce) {
            System.err.println(ce.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lockValues(Integer Key) throws IllegalAccessException {

    }

    @Override
    public void releaseLock(Integer key) {
    
    }

    @Override
    public void dispose() {
    }

}
