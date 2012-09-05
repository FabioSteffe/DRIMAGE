package Outils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class DBRel {

    private static String sDriverName = "org.sqlite.JDBC";
    private static String sJdbc = "jdbc:sqlite";
    private static int iTimeout = 30;
    
    public static void init() throws Exception {
        
        // register the driver 
        if(new File(Config.getDbPath()).exists())
        Class.forName(sDriverName);
        else
            throw new Exception("la base des données n'existe pas");

    }
    
    public static Connection openConnection ( ) throws Exception {
        
        String sDbUrl = sJdbc + ":" + Config.getDbPath() ;
        return ( DriverManager.getConnection(sDbUrl) );
        
    }
    
    public static Statement newStatement ( Connection conn ) throws Exception {
        
        Statement stmt = conn.createStatement();
        stmt.setQueryTimeout(iTimeout);
        return stmt ;
        
    }
    
    public static boolean closeStatement ( Statement stmt ) throws Exception {
        
        try{
            stmt.close();
            return true;
        }
        catch( Exception e ){
            return false;
        }
        
    }
    
    public static boolean closeConnection ( Connection conn ) throws Exception {
        
        try { 
            conn.close(); 
            return true;
        }
        catch (Exception e){
            return false;
        }
        
    }
    
    public static ResultSet executeQuery ( Statement stmt , String instruction ) throws Exception {
        
        return stmt.executeQuery(instruction);

    }

    static void executeUpdate(Statement stmt, String instruction) throws Exception {
        
        stmt.executeUpdate(instruction);
        
    }
    
}