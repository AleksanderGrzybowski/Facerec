package facerec;

import facerec.extract.FaceExtractDto;
import facerec.recognize.RecoStatusDto;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Adapter {
    
    private static Logger log = Logger.getLogger(Adapter.class.getName());
    
    public static RecoStatusDto recognize(byte[] data, String method) {
        File tempFile = writeTempFile(data);
        List<String> params = Arrays.asList(
                Config.REGOGNIZE_BINARY_PATH,
                tempFile.getAbsolutePath(),
                method
        );
        
        try {
            Process process = new ProcessBuilder(params)
                    .directory(new File(Config.CORE_DIR))
                    .start();
            log.info("Started reco process");
            process.waitFor();
            
            if (process.exitValue() != 0) {
                log.info("Face not detected");
                return RecoStatusDto.failure();
            } else {
                String line = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
                log.info("Recognized as: " + line);
                return RecoStatusDto.success(Integer.parseInt(line));
            }
        } catch (IOException | InterruptedException e) {
            log.log(Level.SEVERE, "Backend error", e);
            throw new RuntimeException("Error in backend process", e);
        }
    }
    
    public static FaceExtractDto extractFace(byte[] data) {
        File tempFile = writeTempFile(data);
        List<String> params = Arrays.asList(
                Config.EXTRACT_FACE_BINARY_PATH,
                tempFile.getAbsolutePath()
        );
        
        try {
            Process process = new ProcessBuilder(params)
                    .directory(new File(Config.CORE_DIR))
                    .start();
            log.info("Started face extract process");
            process.waitFor();
            
            if (process.exitValue() != 0) {
                log.info("Face not detected");
                return FaceExtractDto.failure();
            } else {
                byte[] extracted = IOUtils.toByteArray(process.getInputStream());
                log.info("Face extracted, data: " + Arrays.toString(extracted).substring(0, 10) + "...");
                return FaceExtractDto.success(extracted);
            }
        } catch (IOException | InterruptedException e) {
            log.log(Level.SEVERE, "Backend error", e);
            throw new RuntimeException("Error in backend process", e);
        }
    }
    
    private static File writeTempFile(byte[] data) {
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
