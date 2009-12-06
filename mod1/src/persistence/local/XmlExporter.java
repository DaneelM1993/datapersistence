/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.local;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Wizard1993
 */
public class XmlExporter {

    File f;

    public XmlExporter(java.io.File dest) {
        f = dest;
    }

    public void export() throws IOException {
        if (!f.isDirectory()) {
            throw new IOException("not a directory");
        }
        XMLEncoder xmle = exportClienti();
        exportFatture(xmle);

    }

    private XMLEncoder exportClienti() throws FileNotFoundException {
       throw new RuntimeException();
    }

    private void exportFatture(XMLEncoder xmle) throws FileNotFoundException {
       throw new RuntimeException();
    }
    
}
