package Objects;

/**
 *
 * @author Steffenino Fabio
 */

public class ResourceAlgo {
    
    private String resourceId;
    private String name ;
    private String algoId ;
    private String description;
    private String directory ;
    private String uriRes ;
    private boolean active;
    private String user;

    public ResourceAlgo(String resId , String name, String algoId, String descr, String dir, String uriRes, boolean active, String user) {
        this.resourceId = resId;
        this.name = name;
        this.algoId = algoId;
        this.description = descr;
        this.directory = dir;
        this.uriRes = uriRes;
        this.active = active;
        this.user=user;
    }

    public String getAlgoId() {
        return algoId;
    }

    public String getName() {
        return name;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getUriRes() {
        return uriRes;
    }

    public String getDescription() {
        return description;
    }

    public String getDirectory() {
        return directory;
    }

    public boolean isActive() {
        return active;
    }

    public String getUser() {
        return user;
    }
    
}
