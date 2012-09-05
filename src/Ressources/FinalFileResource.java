/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ressources;

import Objects.*;
import Outils.*;
import java.io.File;
import java.util.List;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class FinalFileResource extends ServerResource{
    
    @Get
    public Representation getFile() throws Exception{
        
        // recuperation des identifiants depuis l'URI
        String sessionID = this.getRequest().getAttributes().get("sessionID").toString() ;
        String resourceID = this.getRequest().getAttributes().get("resourceID").toString() ;
        String fileName = this.getRequest().getAttributes().get("fileName").toString() ;

        ResourceAlgo ra = Service.getAlgo(resourceID);
        if(ra==null){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("Resource Not Found ! GET on"+this.getRequest().getResourceRef().toString());
        }
        List<Session> sessionList = Service.getSession(resourceID);
        Session sessionCourant = null;
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
        
        String directory = sessionCourant.getDirectory();
        File file = new File(directory+"\\"+fileName);
        if(!file.exists()){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("FILE not exists ! GET on"+this.getRequest().getResourceRef().toString());
        }
        
        String[] result = fileName.split("\\.");
        String type="";

        try{
            type = result[1];
            System.out.println(type);
        }
        catch(Exception ex){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("INVALID NAME ! GET on"+this.getRequest().getResourceRef().toString());
        }
        
        return FileSystemResource.getRepresentation(file, type);
    }
    
}
