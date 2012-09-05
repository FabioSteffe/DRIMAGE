/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ressources;

import Outils.Service;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class UserResource extends ServerResource{
    
    @Get
    public Representation getForm() {

        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<title>Add new user</title>");
        builder.append("</head>");
        builder.append("<body align='center'>");
        builder.append("<img src=\"logo\" width=\"300\" height=\"90\"/>");
        builder.append("<form enctype=\"multipart/form-data\" method=\"POST\" action=\"");
        builder.append(getRequest().getResourceRef().getIdentifier());
        builder.append("\">");
        builder.append("Username : <input type=\"text\" name=\"username\"><br>");
        builder.append("Password : <input type=\"password\" name=\"password\"><br>");
        builder.append("Mail Address : <input type=\"text\" name=\"address\"><br>");
        builder.append("<input type=\"submit\">");
        builder.append("</body>");
        builder.append("</html>");

        return new StringRepresentation(builder.toString(), MediaType.TEXT_HTML);
    }     

    @Post
    public Representation postForm(Representation entity) throws FileUploadException, Exception {
   
        RestletFileUpload fileUpload = new RestletFileUpload(new DiskFileItemFactory()); 
        List<FileItem> fileItems = fileUpload.parseRepresentation(entity);
        
        String username = fileItems.get(0).getString();
        String password = fileItems.get(1).getString();
        String mail = fileItems.get(2).getString();

        if(username.isEmpty() || password.isEmpty() || mail.isEmpty()){
            return new StringRepresentation("ERROR", MediaType.TEXT_PLAIN);
        }
        if(!Service.isUserPresent(username)){
            Service.addUser(username,password,mail);
            return new StringRepresentation("OK", MediaType.TEXT_PLAIN);
        }
        else
            return new StringRepresentation("USER ALREADY EXISTS", MediaType.TEXT_PLAIN);
        
        
        
    }

}
