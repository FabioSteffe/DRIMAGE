/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ressources;

import Objects.*;
import Outils.*;
import java.util.List;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class SessionResource extends ServerResource {
    
    @Get
    public Representation getReq() throws Exception{
        
        // recuperation des identifiants depuis l'URI
        String sessionID = this.getRequest().getAttributes().get("sessionID").toString() ;
        String resourceID = this.getRequest().getAttributes().get("resourceID").toString();
        Session sessionCourant = null;
        
        ResourceAlgo ra = Service.getAlgo(resourceID);
        if(ra==null){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("Resource Not Found ! GET on"+this.getRequest().getResourceRef().toString());
        }
        
        List<Session> sessionList = Service.getSession(resourceID);

        for(Session s : sessionList){
            if(s.getSessionId().equals(sessionID)){
                sessionCourant = s;
                break;
            }
        }
        if(sessionCourant==null){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("Session not exists ! GET on"+this.getRequest().getResourceRef().toString());
        }
        
        List<Parametre> paralist = Service.getParametreSession(sessionID);
        List<String> outfiles = Service.getResOutput(resourceID);
        
        DomRepresentation sessionDoc = new DomRepresentation();
        Document doc = sessionDoc.getDocument();
        Node appli = doc.createElement("application");
        doc.appendChild(appli);
        Node sess = doc.createElement("session");
        appli.appendChild(sess);
        Node id = doc.createElement("id");
        id.setTextContent(sessionCourant.getSessionId());
        sess.appendChild(id);
        Node sesUri = doc.createElement("uri");
        sesUri.setTextContent(sessionCourant.getUri());
        sess.appendChild(sesUri);
        Node mere= doc.createElement("mother");
        mere.setTextContent(sessionCourant.getResourceMere());
        sess.appendChild(mere);
        Node active= doc.createElement("active");
        active.setTextContent(""+sessionCourant.isActive());
        sess.appendChild(active);
        Node input = doc.createElement("input");
        sess.appendChild(input);
        for(Parametre p : paralist){
                Node ele = doc.createElement("element");
                ele.setTextContent(p.getValeur());
                input.appendChild(ele);
        }
        Node output = doc.createElement("output");
        sess.appendChild(output);
        for(String s : outfiles){
                Node ele = doc.createElement("element");
                ele.setTextContent(sessionCourant.getUri()+"/"+s);
                output.appendChild(ele);
        }
        Node resource = doc.createElement("resource");
        appli.appendChild(resource);
        Node nameR = doc.createElement("name");
        nameR.setTextContent(ra.getName());
        resource.appendChild(nameR);
        Node algo = doc.createElement("algorithme");
        algo.setTextContent(ra.getAlgoId());
        resource.appendChild(algo);
        Node descR = doc.createElement("description");
        descR.setTextContent(ra.getDescription());
        resource.appendChild(descR);
        
        return sessionDoc ;
        
    }

    @Delete
    public Representation stopExecution() throws Exception{
        
        // recuperation des identifiants depuis l'URI
        String sessionID = this.getRequest().getAttributes().get("sessionID").toString() ;
        String resourceID = this.getRequest().getAttributes().get("resourceID").toString();
        Session sessionCourant = null;
        
        ResourceAlgo ra = Service.getAlgo(resourceID);
        if(ra==null){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("Resource Not Found ! GET on"+this.getRequest().getResourceRef().toString());
        }
        
        List<Session> sessionList = Service.getSession(resourceID);

        for(Session s : sessionList){
            if(s.getSessionId().equals(sessionID)){
                sessionCourant = s;
                break;
            }
        }
        if(sessionCourant==null){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("Session not exists ! GET on"+this.getRequest().getResourceRef().toString());
        }
        
        if(sessionCourant.isActive()){
            Process p = Config.remove(sessionID);
            p.destroy();
            Service.setSessionEnd(sessionID);
            return new StringRepresentation("<report><status>OK-DELETED</status></report>",MediaType.APPLICATION_XML);
        }
        else{
            return new StringRepresentation("<report><status>SESSION ALREADY CLOSED</status></report>",MediaType.APPLICATION_XML);
        }
        
    }
    
}