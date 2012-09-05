/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ressources;

import Outils.Config;
import java.io.File;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class LogoResource extends ServerResource {
    
    @Get
    public FileRepresentation getR () throws Exception {
         
        File imgPath = new File( Config.getLogoPath() );
        FileRepresentation rep = new FileRepresentation(imgPath,MediaType.IMAGE_JPEG);

        return(rep);
    } 
    
}
