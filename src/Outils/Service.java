package Outils;

import Objects.Parametre;
import Objects.ResourceAlgo;
import Objects.Session;
import Objects.UploadedFile;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Steffenino Fabio
 * 
 * class that lead the interaction with the database
 * 
 */

public class Service {

    public static int getCountResource() throws Exception {
        
        String count = "SELECT COUNT(*) FROM uploadedfiles WHERE type = 'exe' ";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, count);
        int result = rs.getInt(1);
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return result;
        
    }

    public static int getCountImage() throws Exception {
        
        String count = "SELECT COUNT(*) FROM uploadedfiles WHERE type <> 'exe' ";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, count);
        int result = rs.getInt(1);
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return result;
        
    }

    public static boolean addUploadedFile(UploadedFile file) throws Exception {
        
        String count = " INSERT INTO uploadedfiles VALUES('"+file.getName()+"','"+file.getPath()+"','"+file.getType()+"')";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        DBRel.executeUpdate(st, count);
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return true;
        
    }

    public static UploadedFile getFileObject(String fileID) throws Exception {
        
        String count = "SELECT * FROM uploadedfiles WHERE name = '"+fileID+"' ";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, count);
        
        if(rs.next()==false){
            DBRel.closeStatement(st);
            DBRel.closeConnection(conn);
            return null;
        }
        
        UploadedFile object = new UploadedFile(rs.getString(2),rs.getString(3),rs.getString(1));
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return object ;
    }

    public static List<String> getUriResources() throws Exception{
        String urilist = "SELECT uri FROM resources ";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, urilist);
        List<String> listUri = new LinkedList<String>();
        
        if(rs.next()==false){
            DBRel.closeStatement(st);
            DBRel.closeConnection(conn);
            return null;
        }
        do{
            listUri.add(rs.getString(1));
        }
        while(rs.next());
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return listUri ;
    }

    public static boolean findExe(String fileID) throws Exception{
        
        String findexe = "SELECT * FROM uploadedfiles WHERE name = '"+fileID+"' AND type = 'exe' ";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, findexe);
        boolean flag;
        if(rs.next()==false){
            flag= false;
        }
        else
            flag=true;
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return flag ;
    }

    // da completare
    public static void addResourceAlgo( ResourceAlgo resource, List<String> in , List<String> out ) throws Exception {
        
        String insert = " INSERT INTO resources VALUES('"+resource.getResourceId()+"','"+resource.getName()+"','"+resource.getAlgoId()
                +"','"+resource.getDescription()+"','"+resource.getDirectory()+"','"+resource.getUriRes()+"','"+resource.isActive()+"','"+resource.getUser()+"')";
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        DBRel.executeUpdate(st, insert);
        for(int i=0;i<in.size();i++){
            insert = " INSERT INTO input VALUES ('"+resource.getResourceId()+"','"+in.get(i)+"',"+(i+1)+")";
            DBRel.executeUpdate(st, insert);
        }
        for(int i=0;i<out.size();i++){
            insert = " INSERT INTO output VALUES ('"+resource.getResourceId()+"','"+out.get(i)+"')";
            DBRel.executeUpdate(st, insert);
        }
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);

    }

    public static List<String> getAllFiles() throws Exception {
        
        String findexe = "SELECT name FROM uploadedfiles ";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, findexe);
        List<String> list = new LinkedList<String>();
        
        while(rs.next()){
            list.add(rs.getString(1));
        }
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return list;
    }

    public static boolean isUsed(String fileID) throws Exception {
        String urilist = "SELECT * FROM resources WHERE algoId = '"+fileID+"'";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, urilist);
        boolean flag;
        
        if(rs.next()==false){
            flag=false;
        }
        else
            flag=true;
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return flag ;
    }

    public static ResourceAlgo getAlgo(String resource_id) throws Exception {
        
        String resource = "SELECT * FROM resources WHERE id = '"+resource_id+"'";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, resource);
        ResourceAlgo ra ;
        
        if(rs.next()==false){
            ra=null;
        }
        else
            ra= new ResourceAlgo(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getBoolean(7),rs.getString(8));
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return ra ;
        
    }

    public static List<String> getResInput(String resource_id) throws Exception {
        
        String findIN = "SELECT type , pos FROM input WHERE resource = '"+resource_id+"' ORDER BY pos";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, findIN);
        List<String> list = new LinkedList<String>();
        
        while(rs.next()){
            list.add(rs.getString(1));
        }
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return list;
    }

    public static List<String> getResOutput(String resource_id) throws Exception {
        
        String findOUT = "SELECT file FROM output WHERE resource = '"+resource_id+"' ";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, findOUT);
        List<String> list = new LinkedList<String>();
        
        while(rs.next()){
            list.add(rs.getString(1));
        }
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return list;
        
    }

    public static List<Session> getSession(String resource_id) throws Exception{
        
        String findSes = "SELECT * FROM session WHERE resource = '"+resource_id+"' ";
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        ResultSet rs = DBRel.executeQuery(st, findSes);
        List<Session> list = new LinkedList<Session>();
        
        while(rs.next()){
            
            list.add(new Session(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),Boolean.parseBoolean(rs.getString(5)),rs.getString(6)));
        }
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return list;
        
    }

    public static void addListParametre(List<Parametre> paralist) throws Exception{

        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String insert;
        for(int i=0;i<paralist.size();i++){
            Parametre para = paralist.get(i);
            insert = " INSERT INTO parameters VALUES ('"+para.getSessionID()+"','"+para.getValeur()+"','"+para.getUploaded()+"',"+(i+1)+")";
            DBRel.executeUpdate(st, insert);
        }

        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
    }

    public static void addSession(Session s) throws Exception {
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String insert = " INSERT INTO session VALUES ('"+s.getSessionId()+"','"+s.getUri()+"','"+s.getDirectory()+"','"+s.getResourceMere()+"','"
                + ""+s.isActive()+"','"+s.getUser()+"')";
            
        DBRel.executeUpdate(st, insert);

        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
    }

    public static List<Parametre> getParametreSession(String sessionId) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String find = " SELECT * FROM parameters WHERE session = '"+sessionId+"'";
        List<Parametre> paralist = new LinkedList<Parametre>();
        
        ResultSet rs = DBRel.executeQuery(st, find);
        
        while(rs.next()){
            Parametre para = new Parametre(rs.getString(1),Boolean.parseBoolean(rs.getString(3)),rs.getString(2));
            paralist.add(para);
        }

        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return paralist;
    }

    public static void setSessionEnd(String sessionId) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String update = " UPDATE session SET status = 'false' WHERE id = '"+sessionId+"' " ;
            
        DBRel.executeUpdate(st, update);

        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
    }

    public static boolean isParameterOfActiveSession(String fileID) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String find = " SELECT * FROM parameters p JOIN session s WHERE p.session = s.id AND s.status = 'true' AND  p.input = '"+fileID+"'";
        boolean flag= true;
        
        ResultSet rs = DBRel.executeQuery(st, find);
        
        if(rs.next()==false){
            flag=false;
        }

        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return flag;
        
    }

    public static void deleteFile(String fileID) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String update = " DELETE FROM uploadedfiles WHERE name = '"+fileID+"' " ;
            
        DBRel.executeUpdate(st, update);

        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
    }

    public static void updateResource(String resId , String nameElt, String descrElt, String executable) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String update = " UPDATE resources SET name='"+nameElt+"', algoId='"+executable+"', descri='"+descrElt+"' WHERE id = '"+resId+"' " ;

        DBRel.executeUpdate(st, update);

        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
    }

    public static void updateInput(String resourceId, List<String> in) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String update = " DELETE FROM input WHERE resource = '"+resourceId+"' " ;
            
        DBRel.executeUpdate(st, update);

        for(int i=0;i<in.size();i++){
            String insert = " INSERT INTO input VALUES ('"+resourceId+"','"+in.get(i)+"',"+(i+1)+")";
            DBRel.executeUpdate(st, insert);
        }
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
    }

    public static void updateOutput(String resourceId, List<String> out) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String update = " DELETE FROM output WHERE resource = '"+resourceId+"' " ;
            
        DBRel.executeUpdate(st, update);

        for(int i=0;i<out.size();i++){
            String insert = " INSERT INTO output VALUES ('"+resourceId+"','"+out.get(i)+"')";
            DBRel.executeUpdate(st, insert);
        }
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
    }

    public static void deleteResource(String resId) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String update = " UPDATE resources SET status='false' WHERE id = '"+resId+"' " ;

        DBRel.executeUpdate(st, update);

        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
    }

    public static char[] getPassword(String identifier) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String getPass = " SELECT pass FROM users WHERE id = '"+identifier+"' " ;
        char[] pass = null ;
        
        ResultSet rs = DBRel.executeQuery(st, getPass);
        if(rs.next())
            pass = rs.getString(1).toCharArray();
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return pass;
    }

    public static void addUser(String username, String password, String mail) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String insert = " INSERT INTO users VALUES ('"+username+"','"+password+"','"+mail+"')";
            
        DBRel.executeUpdate(st, insert);

        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
    }

    public static String getUserResource(String string) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String getPass = " SELECT username FROM resources WHERE id = '"+string+"' " ;
        String user = null ;
        
        ResultSet rs = DBRel.executeQuery(st, getPass);
        if(rs.next())
            user = rs.getString(1);
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return user;
        
    }

    public static String getUserSession(String string) throws Exception{
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String getPass = " SELECT username FROM session WHERE id = '"+string+"' " ;
        String user = null ;
        
        ResultSet rs = DBRel.executeQuery(st, getPass);
        if(rs.next())
            user = rs.getString(1);
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return user;
        
    }

    public static boolean isUserPresent(String username) throws Exception {
        
        Connection conn = DBRel.openConnection();
        Statement st = DBRel.newStatement(conn);
        String getPass = " SELECT * FROM users WHERE id = '"+username+"' " ;
        boolean user = false ;
        
        ResultSet rs = DBRel.executeQuery(st, getPass);
        if(rs.next())
            user = true;
        
        DBRel.closeStatement(st);
        DBRel.closeConnection(conn);
        
        return user;
        
    }
    
}