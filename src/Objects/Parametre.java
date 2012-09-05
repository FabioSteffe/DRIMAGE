/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

/**
 *
 * @author Steffenino Fabio
 * 
 */

public class Parametre {
    
    private String sessionID;
    private boolean uploaded;
    private String valeur;

    public Parametre(String sessionID, boolean uploaded, String valeur) {
        this.sessionID = sessionID;
        this.uploaded = uploaded;
        this.valeur = valeur;
    }

    public String getSessionID() {
        return sessionID;
    }

    public boolean getUploaded() {
        return uploaded;
    }

    public String getValeur() {
        return valeur;
    }
    
    
}
