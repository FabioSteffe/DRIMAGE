package Objects;

/**
 *
 * @author Steffenino Fabio
 */

public class UploadedFile {
    
    private String path;
    private String type;
    private String name;

    public UploadedFile(String path, String type, String key) {
        this.path = path;
        this.type = type;
        this.name = key;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }
    
    
    
}
