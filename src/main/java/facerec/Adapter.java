package facerec;

import facerec.extract.FaceExtractDto;
import facerec.recognize.RecoStatusDto;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Adapter {
    
    private Logger log = Logger.getLogger(Adapter.class.getName());
    private Configuration config;
    
    public Adapter(Configuration config) {
        this.config = config;
    }
    
    public RecoStatusDto recognize(byte[] data, String method) {
        File tempFile = writeTempFile(data);
        List<String> params = Arrays.asList(
                config.getString("recognize_binary_path"),
                tempFile.getAbsolutePath(),
                method
        );
        
        try {
            Process process = createAndRunProcess(params);
            
            if (process.exitValue() != 0) {
                log.info("Face not detected");
                return RecoStatusDto.failure();
            } else {
                String line = readLine(process.getInputStream());
                log.info("Recognized as: " + line);
                return RecoStatusDto.success(Integer.parseInt(line));
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Backend error", e);
            throw new RuntimeException("Error in backend process", e);
        }
    }
    
    private String readLine(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.readLine();
        }
    }
    
    public FaceExtractDto extractFace(byte[] data) {
        File tempFile = writeTempFile(data);
        List<String> params = Arrays.asList(
                config.getString("extract_face_binary_path"),
                tempFile.getAbsolutePath()
        );
        
        try {
            Process process = createAndRunProcess(params);
            
            if (process.exitValue() != 0) {
                log.info("Face not detected");
                return FaceExtractDto.failure();
            } else {
                byte[] extracted = IOUtils.toByteArray(process.getInputStream());
                log.info("Face extracted, data: " + Arrays.toString(extracted).substring(0, 10) + "...");
                return FaceExtractDto.success(extracted);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Backend error", e);
            throw new RuntimeException("Error in backend process", e);
        }
    }
    
    private Process createAndRunProcess(List<String> params) throws IOException {
        Process process = new ProcessBuilder(params)
                .directory(new File(config.getString("core_dir")))
                .start();
        log.info("Started process " + params);
        
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new AssertionError(e); // can't happen here
        }
        
        return process;
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
