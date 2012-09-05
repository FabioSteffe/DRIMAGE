/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Ressources;

import Objects.*;
import Outils.Config;
import Outils.Service;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class AlgoResource extends ServerResource {
    
    @Get
    public Representation getAlgoRep() throws Exception{
        
        String resource_id = this.getRequest().getAttributes().get("resourceID").toString();

        ResourceAlgo ra = Service.getAlgo(resource_id);       
        if(ra==null){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("RESOURCE "+resource_id+" NOT FOUND Operation GET on "+this.getRequest().getResourceRef().toString());
        }
        if(!ra.isActive()){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("RESOURCE "+resource_id+" NOT FOUND Operation GET on "+this.getRequest().getResourceRef().toString());
        }
        
        DomRepresentation resourceDesc = new DomRepresentation();
        Document doc = resourceDesc.getDocument();
        Node appli = doc.createElement("application");
        doc.appendChild(appli);
        Node resource = doc.createElement("resource");
        appli.appendChild(resource);
        Node id = doc.createElement("id");
        id.setTextContent(ra.getResourceId());
        resource.appendChild(id);
        Node resUri = doc.createElement("uri");
        resUri.setTextContent(ra.getUriRes());
        resource.appendChild(resUri);
        Node nameR = doc.createElement("name");
        nameR.setTextContent(ra.getName());
        resource.appendChild(nameR);
        Node boss = doc.createElement("boss-user");
        boss.setTextContent(ra.getUser());
        resource.appendChild(boss);
        Node algo = doc.createElement("algorithme");
        algo.setTextContent(ra.getAlgoId());
        resource.appendChild(algo);
        Node descR = doc.createElement("description");
        descR.setTextContent(ra.getDescription());
        resource.appendChild(descR);
        
        List<String> in = Service.getResInput(resource_id);
        
        Node input = doc.createElement("input");
        resource.appendChild(input);
        Node listIn = doc.createElement("list");
        input.appendChild(listIn);
        Attr numElin = doc.createAttribute("numel");
        numElin.setNodeValue(""+in.size()+"");
        listIn.getAttributes().setNamedItem(numElin);
        if(in != null){
            for(String s : in){
                Node uri = doc.createElement("element");
                uri.setTextContent(s);
                listIn.appendChild(uri);
            }
        }
        
        List<String> out = Service.getResOutput(resource_id);
        
        Node output = doc.createElement("output");
        resource.appendChild(output);
        Node listOut = doc.createElement("list");
        output.appendChild(listOut);
        Attr numElout = doc.createAttribute("numel");
        numElout.setNodeValue(""+out.size()+"");
        listOut.getAttributes().setNamedItem(numElout);
        if(out != null){
            for(String s : out){
                Node uri = doc.createElement("element");
                uri.setTextContent(s);
                listOut.appendChild(uri);
            }
        }
        
        List<Session> sessList = Service.getSession(resource_id);
        
        Node sessions = doc.createElement("sessions");
        resource.appendChild(sessions);
        if(out != null){
            for(Session s : sessList){
                Node uri = doc.createElement("uri");
                uri.setTextContent(s.getUri());
                sessions.appendChild(uri);
                Node active = doc.createElement("active");
                active.setTextContent(""+s.isActive());
                sessions.appendChild(active);
            }
        }
        
        return resourceDesc ;
        
    }
    
    @Post
    public Representation makeSession( DomRepresentation dom ) throws Exception {
        
        
        //verifier si la resource existe
        String resource_id = this.getRequest().getAttributes().get("resourceID").toString();
        
        ResourceAlgo ra = Service.getAlgo(resource_id);       
        if(ra==null){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("RESOURCE "+resource_id+" NOT FOUND Operation POST on "+this.getRequest().getResourceRef().toString());
        }
        if(!ra.isActive()){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("RESOURCE "+resource_id+" NOT FOUND Operation GET on "+this.getRequest().getResourceRef().toString());
        }
        
        //session id , uri et directory
        String sessionID = "session"+System.currentTimeMillis();
        String sesURI = this.getRequest().getResourceRef().toString()+"/"+sessionID;
        String dir = ra.getDirectory()+"\\"+sessionID;
        
        //traitement xml
        Element docElt,parameters,list;
        boolean uploaded;
        String fileID ;
        List<Parametre> paralist = new LinkedList<Parametre>();
        
        try{
            Document doc = dom.getDocument();
            docElt = doc.getDocumentElement();
            parameters = (Element) docElt.getElementsByTagName("parameters").item(0);
            list = (Element) parameters.getElementsByTagName("list").item(0);
            int nEl = Integer.parseInt(list.getAttribute("numel"));
            for(int i=0;i<nEl;i++){
                Element element  = (Element) list.getElementsByTagName("element").item(i);
                fileID = element.getTextContent();
                if(fileID.isEmpty())
                    throw new Exception("ERROR-bad xml file-empty slot ! ");
                uploaded = Boolean.parseBoolean(element.getAttribute("uploaded"));
                if(uploaded){
                    UploadedFile file = Service.getFileObject(fileID);
                    if(file==null){
                        throw new Exception("ERROR-bad xml file-input element not present in the database ! ");
                    }
                }
                Parametre para = new Parametre(sessionID,uploaded,fileID);
                paralist.add(para);
            }
        }
        catch( Exception ex){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep(ex.getMessage()+" Operation POST on "+this.getRequest().getResourceRef().toString());
        }

        //new directory
        boolean success = (new File(dir)).mkdir();
        if(success==false){
            this.getResponse().setStatus(Status.valueOf(500));
            return Config.getErrorRep("ERROR mkdir session ! POST on "+this.getRequest().getResourceRef().toString());
        }
        
        // insertion list de parametre dans la base
        Service.addListParametre(paralist);
        
        //on va creer une nouvelle session et on l'ajoute à la base
        Session s = new Session(sessionID,sesURI,dir,resource_id,true,this.getRequest().getChallengeResponse().getIdentifier());
        Service.addSession(s);
        
        //on lance l'execution
        s.run(paralist,ra);
        
        return new StringRepresentation("<session><uri>"+s.getUri()+"</uri></session>",MediaType.APPLICATION_XML);
        
    }
    
    //implementer le PUT et DELETE
    @Put
    public Representation modifyResource ( DomRepresentation r ) throws Exception {
        
         //verifier si la resource existe
        String resource_id = this.getRequest().getAttributes().get("resourceID").toString();
        
        ResourceAlgo ra = Service.getAlgo(resource_id);       
        if(ra==null){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("RESOURCE "+resource_id+" NOT FOUND Operation POST on "+this.getRequest().getResourceRef().toString());
        }
        if(!ra.isActive()){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("RESOURCE "+resource_id+" NOT FOUND Operation GET on "+this.getRequest().getResourceRef().toString());
        }
        
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
            if(nameElt.equals(""))
                nameElt=ra.getName();
            
            descrElt = docElt.getElementsByTagName("descr").item(0).getTextContent();
            if(descrElt.equals(""))
                descrElt=ra.getDescription();
            
            executable = docElt.getElementsByTagName("exe").item(0).getTextContent();
            if(executable.equals(""))
                executable=ra.getAlgoId();
            
            // on regarde si l'executable existe
            if(Service.findExe(executable)){
                Service.updateResource(ra.getResourceId(),nameElt,descrElt,executable);
            }
            
            //lecture input
            //le premiere parametre doit etre le repertoire d'execution ( pas specifié dans la liste )
            inElt = (Element)docElt.getElementsByTagName("input").item(0);
            if(Boolean.parseBoolean(inElt.getAttribute("update"))){
                in = new LinkedList<String>();
                Element listElt =  (Element) inElt.getElementsByTagName("list").item(0);
                int nEl = Integer.parseInt(listElt.getAttribute("numel"));
                for(int i=0;i<nEl;i++){
                    String element = listElt.getElementsByTagName("element").item(i).getTextContent();
                    if(element.isEmpty())
                        throw new Exception("ERROR-bad xml file-empty slot ! ");
                    in.add(element);
                }
                Service.updateInput(ra.getResourceId(),in);
            }

            //lecture output , ogni elemento rappresenta il nome del file prodotto
            outElt = (Element)docElt.getElementsByTagName("output").item(0);
            if(Boolean.parseBoolean(outElt.getAttribute("update"))){
                out = new LinkedList<String>();
                Element listElt =  (Element) outElt.getElementsByTagName("list").item(0);
                int nEl = Integer.parseInt(listElt.getAttribute("numel"));
                for(int i=0;i<nEl;i++){
                    String element = listElt.getElementsByTagName("element").item(i).getTextContent();
                    if(element.isEmpty())
                        throw new Exception("ERROR-bad xml file-empty slot ! ");
                    out.add(element);
                }
                out.add("sortieStandard.txt");
                Service.updateOutput(ra.getResourceId(),out);
            }
        }
        catch(Exception ex){
            this.getResponse().setStatus(Status.valueOf(400));
            return Config.getErrorRep(ex.getMessage()+"PUT on "+ this.getRequest().getResourceRef().toString());
        }

        DomRepresentation reportDoc = new DomRepresentation();
        Document doc = reportDoc.getDocument();
        Node appli = doc.createElement("application");
        doc.appendChild(appli);
        Node report = doc.createElement("report");
        appli.appendChild(report);
        Node operation = doc.createElement("operation");
        operation.setTextContent("PUT");
        report.appendChild(operation);
        Node uriRef = doc.createElement("uriRef");
        uriRef.setTextContent(this.getRequest().getResourceRef().toString());
        report.appendChild(uriRef);
        Node status = doc.createElement("status");
        status.setTextContent("RESOURCE "+ra.getResourceId()+" MODIFIED");
        report.appendChild(status);
        
        return reportDoc;
    }
    
    @Delete
    public Representation deleteResource() throws Exception {
        
        String resource_id = this.getRequest().getAttributes().get("resourceID").toString();

        ResourceAlgo ra = Service.getAlgo(resource_id);       
        if(ra==null){
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("RESOURCE "+resource_id+" NOT FOUND Operation GET on "+this.getRequest().getResourceRef().toString());
        }
        if(ra.isActive()){
            Service.deleteResource(ra.getResourceId());
            return null;
        }
        else{
            this.getResponse().setStatus(Status.valueOf(404));
            return Config.getErrorRep("RESOURCE "+resource_id+" NOT FOUND Operation GET on "+this.getRequest().getResourceRef().toString());
        }
        
    }
    
}
