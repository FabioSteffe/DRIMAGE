/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ressources;

import Objects.UploadedFile;
import Outils.Config;
import Outils.Service;
import java.io.File;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class FileSystemResource extends ServerResource {
    
    @Get
    public Representation getFile() throws Exception{
        
        String fileID = this.getRequest().getAttributes().get("fileID").toString();
        UploadedFile object = Service.getFileObject(fileID);
        
        if( object==null ){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("RESOURCE NOT FOUND");
        }
        
        File file = new File(object.getPath());
        if(!file.exists()){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("ERROR FILE NOT FOUND");
        }
        
        FileRepresentation filerep = getRepresentation(file,object.getType());
        if( filerep == null ){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("ERROR BAD REPRESENTATION");
        }
        
        return filerep;
    }
    
    // da completare dopo l'integrazione delle sessioni
    @Delete
    public Representation deleteFile() throws Exception{
        
        String fileID = this.getRequest().getAttributes().get("fileID").toString();
        
        // si algo on controle qu'il n'est pas lièe à une ressource
        if(fileID.contains("algo") && Service.isUsed(fileID)){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("THE FILE IS USED FROM A RESOURCE");
        }
        // si image on controle s'il est en train d'etre utilisée(da completare dopo l'integrazione delle sessioni )
        if(fileID.contains("img") && Service.isParameterOfActiveSession(fileID)){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("THE FILE IS USED FROM A SESSION");
        }
        
        UploadedFile object = Service.getFileObject(fileID);
        if( object==null ){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("RESOURCE NOT FOUND");
        }
        
        File file = new File(object.getPath());
        if(!file.exists()){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("ERROR FILE NOT FOUND");
        }
        
        try{
            file.delete();
        }
        catch( Exception e ){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep("CANNOT BE DELETED");
        }
        
        Service.deleteFile(fileID);
        
        return new StringRepresentation("<report><status>OK-DELETED</status></report>",MediaType.APPLICATION_XML);
    }
    
    public static FileRepresentation getRepresentation( File file , String type ){
        
        if(type.equalsIgnoreCase("exe")||type.equalsIgnoreCase("txt")){
            return new FileRepresentation(file,MediaType.APPLICATION_OCTET_STREAM);
        }
        if(type.equalsIgnoreCase("jpg")){
            return new FileRepresentation(file,MediaType.IMAGE_JPEG);
        }

        return null;
    }
    
    
}
