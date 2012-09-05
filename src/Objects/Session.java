/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Outils.Config;
import Outils.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

/**
 *
 * @author Steffenino Fabio
 * 
 */
public class Session {
    
    private String sessionId;
    private String uri;
    private String directory;
    private String resourceMere;
    private boolean active;
    private String user;

    public Session(String sessionId, String uri, String directory, String resourceMere, boolean active, String user) {
        this.sessionId = sessionId;
        this.uri=uri;
        this.directory = directory;
        this.resourceMere = resourceMere;
        this.active = active;
        this.user=user;
    }

    public boolean isActive() {
        return active;
    }

    public String getDirectory() {
        return directory;
    }

    public String getResourceMere() {
        return resourceMere;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUser() {
        return user;
    }

    public String getUri() {
        return uri;
    }

    public void run( final List<Parametre> paralist, final ResourceAlgo ra) throws Exception {
        
        if(active==false){
            return ;
        }
        
        Thread t = new Thread ( new Runnable() {

            @Override
            public void run() {
                try {
                    
                    String[] command =new String[2+paralist.size()];
                    command[0]=Config.getFilesDirectory()+"exe\\"+ra.getAlgoId();
                    command[1]=directory+"\\";
                    for(int i=2;i<paralist.size()+2;i++){
                        final Parametre para = paralist.get(i-2);
                        if(para.getUploaded()){
                            command[i]=Config.getFilesDirectory()+"image\\"+para.getValeur();
                        }
                        else{
                            command[i]=para.getValeur();
                        }
                    }
                    ProcessBuilder probuilder = new ProcessBuilder( command );
                    //work directory
                    probuilder.directory(new File(directory+"\\"));
                    Process process = probuilder.start();
                    Config.put(sessionId, process);
                    //lecture sortie standard
                    File out = new File ( directory+"\\sortieStandard.txt" );
                    out.createNewFile();
                    PrintStream ls = new PrintStream(out);
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    ls.println("SORTIE STANDARD DE L'ALGORITHME "+ra.getName());
                    ls.println("-------------------------------------------------------------");
                    while ((line = br.readLine()) != null) {
                        ls.println(line);
                    }
                    
                    try{
                        Service.setSessionEnd(sessionId);
                        Config.remove(sessionId);
                    } 
                    catch (Exception ignore) {   
                    }
                    
                } catch (IOException ex) {
                }

            }
            
        });
        
        t.start();
        
    }
    
}
