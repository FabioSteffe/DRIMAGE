package Ressources;

import Objects.*;
import Outils.Config;
import Outils.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
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

public class UploadResource extends ServerResource {
    
    @Get
    public Representation getRepresentation() {
        
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<title>Upload File</title>");
        builder.append("</head>");
        builder.append("<body align='center'>");
        builder.append("<img src=\"logo\" width=\"300\" height=\"90\"/>");
        builder.append("<form enctype=\"multipart/form-data\" method=\"POST\" action=\"");
        builder.append(getRequest().getResourceRef().getIdentifier());
        builder.append("\">");
        builder.append("<input type=\"file\" name=\"fileToUpload\"><br>");
        builder.append("<input type=\"submit\">");
        builder.append("</body>");
        builder.append("</html>");

        return new StringRepresentation(builder.toString(), MediaType.TEXT_HTML);
    }
    
    @Post
    public Representation store(Representation input) throws Exception {
        
        UploadedFile file;
        
        String reference = "http://"+Config.getAddress()+":"+Config.getPort()+"/server/files/";
        
        if(input.getMediaType().toString().contains("image")){
            
            //on utilise un client pour uploader l'image
            file = extractData(".jpg");
                
        }
        else if( input.getMediaType().toString().equals("application/octet-stream")){
        
            // on utilise un client qui envoi un octect stream
            file = extractData(".exe");
            
        }
        else{
            
            // on a utilisé le FORM pour charger le file
            RestletFileUpload fileUpload = new RestletFileUpload(new DiskFileItemFactory()); 
            if(input == null ){
                this.getResponse().setStatus(Status.valueOf(400));
                return Config.getErrorRep("ERROR null pointer");
            }
            List<FileItem> fileItems = fileUpload.parseRepresentation(input);
            if(fileItems == null ){
                this.getResponse().setStatus(Status.valueOf(400));
                return Config.getErrorRep("ERROR null pointer");
            }
            file = null ;
            if(fileItems.size()!=1){
                this.getResponse().setStatus(Status.valueOf(400));
                return Config.getErrorRep("ERROR bad parameter");
            }

            for (FileItem fileItem : fileItems) {

                file = extractData(fileItem.getName());
                if(file==null){
                    this.getResponse().setStatus(Status.valueOf(400));
                    return Config.getErrorRep("ERROR null pointer");
                }

                System.out.println("saving file  .... ");

                File attachment = new File(file.getPath());
                attachment.createNewFile();
                fileItem.write(attachment);

                //add file au base des donnees
                if(!Service.addUploadedFile( file )){
                    this.getResponse().setStatus(Status.valueOf(500));
                    return Config.getErrorRep("ERROR while uploading");
                }
            }

            return new StringRepresentation("<uri>"+reference+file.getName()+"</uri>",MediaType.APPLICATION_XML );
            
        }
        
        File executable = new File(file.getPath());
        executable.createNewFile();
        System.out.println("saving file  .... ");
        FileOutputStream fos = new FileOutputStream(executable);
        input.write(fos);
        fos.close();
        if(!Service.addUploadedFile( file )){
                this.getResponse().setStatus(Status.valueOf(400));
                return Config.getErrorRep("ERROR while uploading");
        }

        return new StringRepresentation("<uri>"+reference+file.getName()+"</uri>",MediaType.APPLICATION_XML );
    }

    private UploadedFile extractData(String name) throws Exception {
        
        long count = System.currentTimeMillis();
        String directory = Config.getFilesDirectory();

        if(name.contains(".exe")||name.contains(".EXE")){
            return ( new UploadedFile(directory+"exe\\algo"+count+".exe", "exe" ,"algo"+count )) ;
        }
        if(name.contains(".jpg")||name.contains(".JPG")){
            return ( new UploadedFile(directory+"image\\img"+count+".jpg" , "jpg" , "img"+count ));
        }
        
        return null ;
    }
    
}
