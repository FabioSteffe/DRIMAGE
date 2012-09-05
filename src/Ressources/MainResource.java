/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ressources;

import Objects.*;
import Outils.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class MainResource extends ServerResource {

    final public String name = "Application d'analyse de l'image";
    final public String descrip ="implemented with RESTlet framework";
    final public String autore ="Fabio";
    final public String lastName ="Steffenino";
    final public String contact ="fabio.steffenino@insa-lyon.fr";
    
    @Get
    public Representation GetXml ( Variant variant ) throws Exception{

        DomRepresentation serverDesc = new DomRepresentation();
        
        serverDesc.setIndenting(true);
        serverDesc.setNamespaceAware(true);
        
        Document doc = serverDesc.getDocument();

        Node appli = doc.createElement("application");
        doc.appendChild(appli);
        
        Node server = doc.createElement("server");
        appli.appendChild(server);
        Node nameElt = doc.createElement("name");
        nameElt.setTextContent(name);
        server.appendChild(nameElt);
        Node descElt = doc.createElement("description");
        descElt.setTextContent(descrip);
        server.appendChild(descElt);
        Node accountRefElt = doc.createElement("URI");
        accountRefElt.setTextContent(new Reference(getReference(), "").getTargetRef().toString());
        server.appendChild(accountRefElt);
        
        Node dieu = doc.createElement("god");
        appli.appendChild(dieu);
        Node nomDieu = doc.createElement("name");
        nomDieu.setTextContent(autore);
        dieu.appendChild(nomDieu);
        Node lastDieu = doc.createElement("lastname");
        lastDieu.setTextContent(lastName);
        dieu.appendChild(lastDieu);
        Node conDieu = doc.createElement("contact");
        conDieu.setTextContent(contact);
        dieu.appendChild(conDieu);
        
        Node router = doc.createElement("router");
        appli.appendChild(router);
        Node logo = doc.createElement("logo");
        router.appendChild(logo);
        Node uriLogo = doc.createElement("uri");
        uriLogo.setTextContent("http://127.0.0.1:8111/server/logo");
        logo.appendChild(uriLogo);
        Node xmlReq = doc.createElement("xmlrequest");
        router.appendChild(xmlReq);
        Node uri1 = doc.createElement("uri");
        uri1.setTextContent("http://127.0.0.1:8111/server/xml/Algo");
        xmlReq.appendChild(uri1);
        Node uri5 = doc.createElement("uri");
        uri5.setTextContent("http://127.0.0.1:8111/server/xml/Update");
        xmlReq.appendChild(uri5);
        Node uri2 = doc.createElement("uri");
        uri2.setTextContent("http://127.0.0.1:8111/server/xml/Session");
        xmlReq.appendChild(uri2); 
        Node user = doc.createElement("newuser");
        router.appendChild(user);
        Node uri0 = doc.createElement("uri");
        uri0.setTextContent("http://127.0.0.1:8111/server/users");
        user.appendChild(uri0);
        Node upload = doc.createElement("newupload");
        router.appendChild(upload);
        Node uri4 = doc.createElement("uri");
        uri4.setTextContent("http://127.0.0.1:8111/server/upload");
        upload.appendChild(uri4);
        Node filesList = doc.createElement("uploadedfiles");
        router.appendChild(filesList);
        Node uri3 = doc.createElement("uri");
        uri3.setTextContent("http://127.0.0.1:8111/server/files");
        filesList.appendChild(uri3);
        Node resources = doc.createElement("resources");
        router.appendChild(resources);
        
        // on recupere l'uri de toutes les ressources
        List<String> listUri = Service.getUriResources();
        if(listUri != null){
            for(String s : listUri){
                Node uri = doc.createElement("uri");
                uri.setTextContent(s);
                resources.appendChild(uri);
            }
        }
        
        return serverDesc;
        
    }
   
    @Post
    public Representation putResource ( DomRepresentation r ) throws Exception {
        
        //lecture du text xml reçu
        Element docElt,inElt,outElt;
        String nameElt,executable,descrElt;
        ResourceAlgo resource = null;
        List<String> in = null;
        List<String> out = null;
        try{
            Document doc = r.getDocument();
            docElt = doc.getDocumentElement();
            nameElt = docElt.getElementsByTagName("name").item(0).getTextContent();
            descrElt = docElt.getElementsByTagName("descr").item(0).getTextContent();
            executable = docElt.getElementsByTagName("exe").item(0).getTextContent();
            if(nameElt.equals("") || executable.equals("") || descrElt.equals("")){
                throw new Exception("ERROR-bad xml file-empty slot ! ");
            }
            
            //lecture input
            //le premiere parametre doit etre le repertoire d'execution ( pas specifié dans la liste )
            inElt = (Element)docElt.getElementsByTagName("input").item(0);
            in = new LinkedList<String>();
            Element listElt =  (Element) inElt.getElementsByTagName("list").item(0);
            int nEl = Integer.parseInt(listElt.getAttribute("numel"));
            for(int i=0;i<nEl;i++){
                String element = listElt.getElementsByTagName("element").item(i).getTextContent();
                if(element.isEmpty())
                    throw new Exception("ERROR-bad xml file-empty slot ! ");
                in.add(element);
            }
            
            //lecture output , ogni elemento rappresenta il nome del file prodotto
            outElt = (Element)docElt.getElementsByTagName("output").item(0);
            out = new LinkedList<String>();
            listElt =  (Element) outElt.getElementsByTagName("list").item(0);
            nEl = Integer.parseInt(listElt.getAttribute("numel"));
            for(int i=0;i<nEl;i++){
                String element = listElt.getElementsByTagName("element").item(i).getTextContent();
                if(element.isEmpty())
                    throw new Exception("ERROR-bad xml file-empty slot ! ");
                out.add(element);
            }
            out.add("sortieStandard.txt");
            
            // on regarde si l'executable existe
            if(Service.findExe(executable)){
                
                // resource id et URI
                String resourceId = "resource"+System.currentTimeMillis();
                String resUri = this.getRequest().getResourceRef().toString()+"/"+resourceId;
                String dir = Config.getResDirectory()+resourceId;
                
                //creation nouvelle resource
                resource = new ResourceAlgo(resourceId,nameElt,executable,descrElt,dir,resUri,true,this.getRequest().getChallengeResponse().getIdentifier());
                
                //creation directory de travail
                if(!(new File(dir).mkdir())){
                    throw new Exception("ERROR while creating a new directory "+resourceId);
                }
                
                // on insere la ressource
                Service.addResourceAlgo(resource,in,out);
            }
            
        }
        catch(Exception ex){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep(ex.getMessage()+"POST on "+ this.getRequest().getResourceRef().toString());
        } 
        
        DomRepresentation reportDoc = new DomRepresentation();
        Document doc = reportDoc.getDocument();
        Node appli = doc.createElement("application");
        doc.appendChild(appli);
        Node report = doc.createElement("report");
        appli.appendChild(report);
        Node operation = doc.createElement("operation");
        operation.setTextContent("POST");
        report.appendChild(operation);
        Node uriRef = doc.createElement("uriRef");
        uriRef.setTextContent(this.getRequest().getResourceRef().toString());
        report.appendChild(uriRef);
        Node status = doc.createElement("status");
        status.setTextContent("RESOURCE "+resource.getName()+" ADDED");
        report.appendChild(status);
        Node resNode = doc.createElement("resource");
        appli.appendChild(resNode);
        Node id = doc.createElement("id");
        id.setTextContent(resource.getResourceId());
        resNode.appendChild(id);
        Node resUri = doc.createElement("uri");
        resUri.setTextContent(resource.getUriRes());
        resNode.appendChild(resUri);
        Node nameR = doc.createElement("name");
        nameR.setTextContent(resource.getName());
        resNode.appendChild(nameR);
        Node algo = doc.createElement("algorithme");
        algo.setTextContent(resource.getAlgoId());
        resNode.appendChild(algo);
        Node descR = doc.createElement("description");
        descR.setTextContent(resource.getDescription());
        resNode.appendChild(descR);
        Node input = doc.createElement("input");
        resNode.appendChild(input);
        if(in != null){
            for(String s : in){
                Node uri = doc.createElement("element");
                uri.setTextContent(s);
                input.appendChild(uri);
            }
        }
        Node output = doc.createElement("output");
        resNode.appendChild(output);
        if(out != null){
            for(String s : out){
                Node uri = doc.createElement("element");
                uri.setTextContent(s);
                output.appendChild(uri);
            }
        }
        
        return reportDoc ;
    }

}