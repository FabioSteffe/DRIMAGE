package drimage;

import Objects.MyVerifier;
import Outils.*;
import Ressources.*;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class DRIMAGE extends Application {
    
    public static void main(String[] args) throws Exception {
        
        if(args.length>0 && args[0].contains("-TEST")){
            Config.setVariableTest();
            System.out.println("Test-Mode");
        }
        
        runServer(Protocol.HTTP, Config.getAddress() , Config.getPort() );
        
    }
    
    public static void runServer ( Protocol protocol , String address , int port ) throws Exception {
        
        Component monServer = new Component();
        Server server = new Server( protocol , address , port );
        monServer.getServers().add(server);
        Application appli = new DRIMAGE();
        monServer.getDefaultHost().attach("",appli);
        DBRel.init();
        monServer.start();
    }
    
    @Override
    public Restlet createInboundRoot() {
  
        Router router = new Router(getContext());
        
        // page de default
        router.attachDefault(LogoResource.class);
        
        router.attach("/server",MainResource.class);
        router.attach("/server/users",UserResource.class);
        router.attach("/server/upload",UploadResource.class);
        router.attach("/server/logo",LogoResource.class);
        router.attach("/server/files",FileSystemListResource.class);
        router.attach("/server/files/{fileID}",FileSystemResource.class);
        router.attach("/server/xml/{id}",ResourceXml.class);
        router.attach("/server/{resourceID}",AlgoResource.class);
        router.attach("/server/{resourceID}/{sessionID}",SessionResource.class);
        router.attach("/server/{resourceID}/{sessionID}/{fileName}",FinalFileResource.class);
     
        ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, " HOLA ");  
        MyVerifier verifier = new MyVerifier();
        guard.setVerifier(verifier); 
        guard.setNext(router); 

        return guard;

    }
    
    
}
