package facerec;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class Utils {
    public static File createTemporaryJpgFile(byte[] data) {
        try {
            File file = File.createTempFile("facerec-tmp", ".jpg");
            FileUtils.writeByteArrayToFile(file, data);
            return file;
        } catch (Exception e) {
            throw new AssertionError("Could not create temp file", e);
        }
    }
}
