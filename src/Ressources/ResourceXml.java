/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ressources;

import Outils.Config;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class ResourceXml extends ServerResource {
    
    @Get
    public Representation getRepXml ( ) throws Exception{
        
        String resource_id = this.getRequest().getAttributes().get("id").toString();
      
        DomRepresentation repre = new DomRepresentation();
        Document doc = repre.getDocument();
        
        if( resource_id.equalsIgnoreCase("Algo") ){
            
            
            Node message = doc.createElement("message");
            doc.appendChild(message);
            Node newRes = doc.createElement("newresource");
            message.appendChild(newRes);
            Node name = doc.createElement("name");
            newRes.appendChild(name);
            Node desc = doc.createElement("descr");
            newRes.appendChild(desc);
            Node exe = doc.createElement("exe");
            newRes.appendChild(exe);
            Node in = doc.createElement("input");
            newRes.appendChild(in);
            Attr numelin = doc.createAttribute("numel");
            Node listin = doc.createElement("list");
            in.appendChild(listin);
            listin.getAttributes().setNamedItem(numelin);
            Node elin = doc.createElement("element");
            listin.appendChild(elin);
            Node out = doc.createElement("output");
            newRes.appendChild(out);
            Attr numelout = doc.createAttribute("numel");
            Node listout = doc.createElement("list");
            out.appendChild(listout);
            listout.getAttributes().setNamedItem(numelout);
            Node elout = doc.createElement("element");
            listout.appendChild(elout);
            
        }
        else if(resource_id.equalsIgnoreCase("Session")){
            
            Node message = doc.createElement("message");
            doc.appendChild(message);
            Node newRes = doc.createElement("newsession");
            message.appendChild(newRes);
            Node in = doc.createElement("parameters");
            newRes.appendChild(in);
            Attr numelin = doc.createAttribute("numel");
            Node listin = doc.createElement("list");
            in.appendChild(listin);
            listin.getAttributes().setNamedItem(numelin);
            Node elin = doc.createElement("element");
            listin.appendChild(elin);
            Attr type = doc.createAttribute("uploaded");
            elin.getAttributes().setNamedItem(type);
            
        }
        else if(resource_id.equalsIgnoreCase("Update")){
            
            Node message = doc.createElement("message");
            doc.appendChild(message);
            Node newRes = doc.createElement("updateresource");
            message.appendChild(newRes);
            Node name = doc.createElement("name");
            newRes.appendChild(name);
            Node desc = doc.createElement("descr");
            newRes.appendChild(desc);
            Node exe = doc.createElement("exe");
            newRes.appendChild(exe);
            Node in = doc.createElement("input");
            newRes.appendChild(in);
            Attr update = doc.createAttribute("update");
            in.getAttributes().setNamedItem(update);
            Attr numelin = doc.createAttribute("numel");
            Node listin = doc.createElement("list");
            in.appendChild(listin);
            listin.getAttributes().setNamedItem(numelin);
            Node elin = doc.createElement("element");
            listin.appendChild(elin);
            Node out = doc.createElement("output");
            newRes.appendChild(out);
            Attr update2 = doc.createAttribute("update");
            out.getAttributes().setNamedItem(update2);
            Attr numelout = doc.createAttribute("numel");
            Node listout = doc.createElement("list");
            out.appendChild(listout);
            listout.getAttributes().setNamedItem(numelout);
            Node elout = doc.createElement("element");
            listout.appendChild(elout);
            
        }
        else{
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("RESOURCE xml/"+resource_id+" NOT FOUND ! Operation GET on "+this.getRequest().getResourceRef().toString());
        }
        
        return repre ;
        
    }
    
    
}
