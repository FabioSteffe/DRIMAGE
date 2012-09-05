/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ressources;

import Outils.Service;
import java.util.List;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Steffenino Fabio
 */
public class FileSystemListResource extends ServerResource {
    
    @Get
    public Representation getListFiles() throws Exception {
        
        List<String> files=null;
        try{
            files = Service.getAllFiles();
        }
        catch( Exception ex ){
            this.getResponse().setStatus(Status.valueOf(400));
            return new StringRepresentation("ERROR while extracting the files list");
        }
        
        DomRepresentation domApp = new DomRepresentation();
        
        domApp.setIndenting(true);
        domApp.setNamespaceAware(true);
        
        Document doc = domApp.getDocument();

        Node appli = doc.createElement("application");
        doc.appendChild(appli);
        Node router = doc.createElement("router");
        appli.appendChild(router);
        Node filesList = doc.createElement("uploadedfiles");
        router.appendChild(filesList);
        Node list = doc.createElement("list");
        filesList.appendChild(list);
        
        for(String s : files){
                Node uri = doc.createElement("uri");
                uri.setTextContent(this.getRequest().getResourceRef().toString()+"/"+s);
                list.appendChild(uri);
        }
        
        return domApp;
    }
    
}
