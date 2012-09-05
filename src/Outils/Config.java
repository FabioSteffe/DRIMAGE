package Outils;

import java.util.Map;
import java.util.TreeMap;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Steffenino Fabio
 */

public class Config {
    
    static private int port = 8111;
    static private String address = "127.0.0.1" ;
    static private String directory = "C:\\applicationDrimage\\" ;
    static private String resDirectory = "C:\\applicationDrimage\\resources\\" ;
    static private String filesDirectory = "C:\\applicationDrimage\\files\\" ;
    static private String logoPath = "C:\\applicationDrimage\\logo\\drimage.jpg" ;
    static private String dbPath = "C:\\applicationDrimage\\database\\drimage.db" ;
    static private Map< String,Process > execution = new TreeMap< String,Process >();

    public static String getLogoPath() {
        return logoPath;
    }

    public static String getResDirectory() {
        return resDirectory;
    }

    public static String getDbPath() {
        return dbPath;
    }

    public static String getDirectory() {
        return directory;
    }

    public static String getFilesDirectory() {
        return filesDirectory;
    }

    public static String getAddress() {
        return address;
    }

    public static int getPort() {
        return port;
    }
    
    public static void put( String sessionID , Process proc ) {
        execution.put(sessionID, proc);
    }
    
    public static Process remove( String sessionID ) {
        return execution.remove(sessionID);
    }
    
    public static Representation getErrorRep( String message ) throws Exception {
        
        DomRepresentation errorDesc = new DomRepresentation();
        
        errorDesc.setIndenting(true);
        errorDesc.setNamespaceAware(true);
        
        Document doc = errorDesc.getDocument();

        Node appli = doc.createElement("application");
        doc.appendChild(appli);
        Node report = doc.createElement("report");
        appli.appendChild(report);
        Node error = doc.createElement("error");
        report.appendChild(error);
        Node nameElt = doc.createElement("message");
        nameElt.setTextContent(message);
        error.appendChild(nameElt);
        
        return errorDesc;
    }

    public static void setVariableTest() {
        
        directory = "C:\\applicationDrimage\\test" ;
        resDirectory = "C:\\applicationDrimage\\test\\resources\\" ;
        filesDirectory = "C:\\applicationDrimage\\test\\files\\" ;
        logoPath = "C:\\applicationDrimage\\logo\\drimage.jpg" ;
        dbPath = "C:\\applicationDrimage\\test\\database\\drimage.db" ;
        
    }
    
}
