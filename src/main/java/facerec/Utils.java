package facerec;

import java.io.File;
import java.io.FileOutputStream;

public class Utils {
    static File writeTempFile(byte[] data) {
        try {
            File file = File.createTempFile("facerec-tmp", ".jpg");
            
            FileOutputStream stream = new FileOutputStream(file.getAbsolutePath());
            stream.write(data);
            stream.close();
            
            return file;
        } catch (Exception e) {
            throw new AssertionError("Could not create temp file", e);
        }
    }
}
