package facerec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Adapter {
    public static RecoStatus recognize(byte[] data) throws Exception {
        File tempFile = File.createTempFile("facerec-tmp", "jpg");
        
        FileOutputStream fos = new FileOutputStream(tempFile.getAbsolutePath());
        fos.write(data);
        fos.close();

        Process process = new ProcessBuilder(Arrays.asList("../core/recognize", tempFile.getAbsolutePath()))
                .directory(new File("../core"))
                .start();
        process.waitFor();
        
        if (process.exitValue() != 0) {
            return RecoStatus.failure();
        } else {
            String line = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
            return RecoStatus.success(Integer.parseInt(line));
        }
    }
}
