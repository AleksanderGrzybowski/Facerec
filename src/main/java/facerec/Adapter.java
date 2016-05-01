package facerec;

import facerec.extract.FaceExtractDto;
import facerec.recognize.RecoStatusDto;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Adapter {
    
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
            process.waitFor();
            
            if (process.exitValue() != 0) {
                return RecoStatusDto.failure();
            } else {
                String line = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
                return RecoStatusDto.success(Integer.parseInt(line));
            }
        } catch (IOException | InterruptedException e) {
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
            process.waitFor();
            
            if (process.exitValue() != 0) {
                return FaceExtractDto.failure();
            } else {
                byte[] extracted = IOUtils.toByteArray(process.getInputStream());
                return FaceExtractDto.success(extracted);
            }
        } catch (IOException | InterruptedException e) {
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
