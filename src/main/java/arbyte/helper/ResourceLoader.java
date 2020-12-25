package arbyte.helper;

import java.io.File;
import java.net.URL;

public class ResourceLoader {
    public File getFileFromResource(String filename) throws Exception {
        return new File(getURLFromResource(filename).toURI());

    }

    public URL getURLFromResource(String filename)throws IllegalArgumentException{
        ClassLoader cl = getClass().getClassLoader();
        URL url = cl.getResource(filename);
        if(url == null){
            throw new IllegalArgumentException("File not found : " + filename);
        }
        else{
            return url;
        }

    }
}
