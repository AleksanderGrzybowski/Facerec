package facerec;

import facerec.extract.FaceExtractDto;
import facerec.recognize.RecoStatusDto;
import org.apache.commons.configuration2.Configuration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

public class Adapter {
    
    public static final String HAAR_CLASSIFIER_PATH = "haarcascade_frontalface_alt.xml";
    public static final int FINAL_WIDTH = 92;
    public static final int FINAL_HEIGHT = 112;
    public static final int DEPTH = 8;
    public static final int CHANNELS = 1;
    
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
                return RecoStatusDto.success(predictionValueToName(Integer.parseInt(line)));
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
    
    private String predictionValueToName(int predictionValue) {
        String name = config.getString("mapping_" + predictionValue);
        return (name == null) ? ("id = " + predictionValue) : name;
    }
    
    public FaceExtractDto extractFace(byte[] data) {
        CvHaarClassifierCascade cascadeClassifier = new CvHaarClassifierCascade(cvLoad(HAAR_CLASSIFIER_PATH));
        
        IplImage frame = new IplImage(imread(writeTempFile(data).getAbsolutePath()));
        IplImage grayFrame = cvCreateImage(cvGetSize(frame), DEPTH, CHANNELS);
        cvCvtColor(frame, grayFrame, CV_BGR2GRAY); 
        
        CvSeq detectedFaces = cvHaarDetectObjects(
                grayFrame,
                cascadeClassifier,
                CvMemStorage.create(),
                1.1,
                3,
                CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH
        );
    
        CvRect rectangleWithDetectedFace = new CvRect(cvGetSeqElem(detectedFaces, 0)); // TODO: multiple faces
        
        cvSetImageROI(grayFrame, rectangleWithDetectedFace);
        IplImage cropped = cvCreateImage(
                new CvSize(rectangleWithDetectedFace.width(),
                        rectangleWithDetectedFace.height()), DEPTH, CHANNELS
        );
        cvCopy(grayFrame, cropped);
        
        IplImage resized = cvCreateImage(new CvSize(FINAL_WIDTH, FINAL_HEIGHT), DEPTH, CHANNELS);
        cvResize(cropped, resized);
    
        try {
            File result = File.createTempFile("output", ".jpg");
            cvSaveImage(result.getAbsolutePath(), resized);
            return FaceExtractDto.success(Files.readAllBytes(Paths.get(result.getAbsolutePath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
