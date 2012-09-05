/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Outils.Service;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.User;
import org.restlet.security.Verifier;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class MyVerifier implements Verifier {
   
    public MyVerifier() {
    }
    
    protected String getIdentifier(Request request, Response response) {
        return request.getChallengeResponse().getIdentifier();
    }
    
    protected char[] getSecret(Request request, Response response) {
        return request.getChallengeResponse().getSecret();
    }
    
    @Override
    public int verify(Request request, Response response) {
        
        if(request.getResourceRef().getPath().endsWith("/")){
            String path = request.getResourceRef().getPath();
            path = path.substring(0,path.length()-1);
            request.getResourceRef().setPath(path);
        }
        
        if(resourceFree(request)){
            return RESULT_VALID;
        }
        
        int result = RESULT_VALID;

        if (request.getChallengeResponse() == null) {
            result = RESULT_MISSING;
        } else {
            String identifier = getIdentifier(request, response);
            char[] secret = getSecret(request, response);

            try {
                if (control(identifier, secret, request, response)) {
                    request.getClientInfo().setUser(new User(identifier));
                } else {
                    result = RESULT_INVALID;
                }
            } catch (IllegalArgumentException iae) {
                // The identifier is unknown.
                result = RESULT_UNKNOWN;
            }
        }

        return result;
    }
    
    public boolean control(String identifier, char[] secret, Request request, Response response) {
        
        //System.out.println(request.getMethod().toString());
        
        //search and verify the password  
        char[] pass=null;
        try{
            pass = Service.getPassword(identifier);
            if(pass==null || secret==null){
                throw new Exception("users not found");
            }
            else{
                if(pass.length != secret.length){
                    throw new Exception("password not correct");
                }
                else{
                    
                    boolean equals=true;
                    int i;
                    
                    for (i = 0; (i < pass.length) && equals; i++) {
                        equals = (pass[i] == secret[i]);
                    }
                    if(equals==false || i<secret.length){
                        throw new Exception("password not correct");
                    }

                }  
            }
        }
        catch( Exception ex ){
            return false;
        }
        
        //we verify if the user can do access to the resource
        String uri = request.getResourceRef().getPath();
        String[] elements = uri.split("\\/");
        if(isUpload(elements)||isFileUploaded(elements)){
            return true;
        }
        else if(isResourceAlgo(elements)){
            if(request.getMethod().toString().equalsIgnoreCase("delete")||request.getMethod().toString().equalsIgnoreCase("put")){
                try {
                    if(!(Service.getUserResource(elements[2]).equals(identifier))){
                        return false;
                    }
                } 
                catch (Exception ignore) {
                }
            }
        }
        else if(isSession(elements)||isFinalFile(elements)){
            try {
                if(!(Service.getUserSession(elements[3]).equals(identifier))){
                    return false;
                }
            } catch (Exception ignore) {
            }
        }
        else 
            return false;
        
        return true;
    }

    private boolean resourceFree(Request request) {
        
        String uri = request.getResourceRef().getPath();
        String method = request.getMethod().toString();
        //System.out.println(uri);

        if((uri.equals("/server") && method.equalsIgnoreCase("get")) || uri.equals("/server/logo") || uri.equals("/favicon.ico") ||
                uri.contains("/server/xml/") || uri.equals("/server/files") || uri.equals("/server/users"))
            return true;
        else
            return false;
    }

    private boolean isUpload(String[] elements) {
        if(elements.length==3 && elements[2].equals("upload"))
            return true;
        else
            return false;
    }

    private boolean isFileUploaded(String[] elements) {
        if(elements.length==4 && elements[2].equals("files") && (elements[3].contains("img")||elements[3].contains("algo")))
            return true;
        else
            return false;
    }

    private boolean isResourceAlgo(String[] elements) {
        if(elements.length==3 && elements[2].contains("resource"))
            return true;
        else
            return false;
    }

    private boolean isSession(String[] elements) {
        if(elements.length==4 && elements[2].contains("resource") && elements[3].contains("session"))
            return true;
        else
            return false;
    }

    private boolean isFinalFile(String[] elements) {
        if(elements.length==5 && elements[2].contains("resource") && elements[3].contains("session") && !elements[4].isEmpty())
            return true;
        else
            return false;
    }
    
}