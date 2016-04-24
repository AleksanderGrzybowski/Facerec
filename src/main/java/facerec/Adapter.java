package facerec;

import java.io.*;
import java.util.Arrays;

public class Adapter {
    
    public static final String REGOGNIZE_BINARY_PATH = "./recognize";
    public static final String RECOGNIZE_BINARY_BASEDIR = "core";
    
    public static RecoStatus recognize(byte[] data) {
        File tempFile = writeTempFile(data);
        
        try {
            Process process = new ProcessBuilder(Arrays.asList(REGOGNIZE_BINARY_PATH, tempFile.getAbsolutePath()))
                    .directory(new File(RECOGNIZE_BINARY_BASEDIR))
                    .start();
            process.waitFor();
            
            if (process.exitValue() != 0) {
                return RecoStatus.failure();
            } else {
                String line = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
                return RecoStatus.success(Integer.parseInt(line));
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error in backend process", e);
        }
    }
    
    private static File writeTempFile(byte[] data) {
        try {
            File file = File.createTempFile("facerec-tmp", "jpg");
            
            FileOutputStream stream = new FileOutputStream(file.getAbsolutePath());
            stream.write(data);
            stream.close();
            
            return file;
        } catch (Exception e) {
            throw new AssertionError("Could not create temp file", e);
        }
    }
}
