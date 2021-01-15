package arbyte.helper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceLoader {
    // Returns the file if it does not exist,
    // otherwise create the file then return it
    public File getFileOrCreate(String filename) throws IOException {
        File f = getFile(filename);
        File parentDir = f.getParentFile();

        Files.createDirectories(parentDir.toPath());
        if (f.createNewFile()) {
            System.out.println("File is missing, creating file...");
        }

        return f;
    }

    public File getFile(String filename) {
        return Paths.get(getPathFromRelative(filename)).toFile();
    }

    public URL getURL(String filename) throws Exception {
        return Paths.get(getPathFromRelative(filename)).toUri().toURL();
    }

    private String getPathFromRelative(String relPath) {
        return "src/main/resources/" + relPath;
    }
}
