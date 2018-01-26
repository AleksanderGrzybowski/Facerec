package facerec.core;

import facerec.dto.FaceExtractionResultDto;
import facerec.dto.RecognitionStatusDto;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

@Log
public class FaceService {
    
    private static final String HAAR_CLASSIFIER_RESOURCE_NAME = "haarcascade_frontalface_alt.xml";
    private static final int FINAL_WIDTH = 92;
    private static final int FINAL_HEIGHT = 112;
    
    private static final int DEPTH = 8;
    private static final int CHANNELS = 1;
    
    private final PredictionMappings predictionMappings;
    private final String haarTemporaryLocation;
    private final String modelFilename;
    
    public FaceService(String modelFilename, PredictionMappings predictionMappings) throws Exception {
        this.modelFilename = modelFilename;
        this.predictionMappings = predictionMappings;
        
        this.haarTemporaryLocation = setupHaarFile();
    }
    
    public RecognitionStatusDto recognize(byte[] data) {
        FaceRecognizer model = createEigenFaceRecognizer();
        
        log.info("Loading model from " + modelFilename);
        model.load(modelFilename);
        
        IplImage frame = new IplImage(imread(createTemporaryJpgFile(data).getAbsolutePath()));
        log.info("Image file loaded, dimensions: " + frame.width() + "x" + frame.height());
        
        IplImage grayFrame = convertToGrayscale(frame);
        
        CvSeq detectedFaces = detectFaces(grayFrame);
        if (detectedFaces.total() == 0) {
            log.info("No faces detected");
            return RecognitionStatusDto.failure();
        } else {
            log.info("Face detected!");
        }
        
        CvRect rectangleWithDetectedFace = new CvRect(cvGetSeqElem(detectedFaces, 0));
        
        cvSetImageROI(grayFrame, rectangleWithDetectedFace);
        IplImage cropped = cvCreateImage(
                new CvSize(
                        rectangleWithDetectedFace.width(),
                        rectangleWithDetectedFace.height()
                ),
                DEPTH,
                CHANNELS
        );
        cvCopy(grayFrame, cropped);
        
        IplImage resized = cvCreateImage(new CvSize(FINAL_WIDTH, FINAL_HEIGHT), DEPTH, CHANNELS);
        cvResize(cropped, resized);
        
        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        model.predict(new Mat(resized), label, confidence);
        
        log.info("Image recognized as label " + label.get(0) + " with confidence level " + confidence.get(0));
        
        int predictionValue = label.get(0);
        return RecognitionStatusDto.success(
                predictionMappings.descriptionForValue(predictionValue).orElse("id = " + predictionValue)
        );
    }
    
    public FaceExtractionResultDto extractFace(byte[] data) {
        IplImage frame = new IplImage(imread(createTemporaryJpgFile(data).getAbsolutePath()));
        IplImage grayFrame = convertToGrayscale(frame);
        
        CvSeq detectedFaces = detectFaces(grayFrame);
        
        CvRect rectangleWithDetectedFace = new CvRect(cvGetSeqElem(detectedFaces, 0));
        if (detectedFaces.total() == 0) {
            log.info("No faces detected");
            return FaceExtractionResultDto.failure();
        } else {
            log.info("Face detected!");
        }
        
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
            return FaceExtractionResultDto.success(Files.readAllBytes(Paths.get(result.getAbsolutePath())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String setupHaarFile() throws IOException {
        URL resourceUrl = getClass().getResource("/" + HAAR_CLASSIFIER_RESOURCE_NAME);
        
        File temporaryHaarFile = File.createTempFile("facerec-haar", ".xml");
        FileUtils.copyURLToFile(resourceUrl, temporaryHaarFile);
        
        String haarLocation = temporaryHaarFile.getAbsolutePath();
        log.info("Haar xml file copied to temporary location " + haarLocation);
        return haarLocation;
    }
    
    private IplImage convertToGrayscale(IplImage frame) {
        IplImage grayFrame = cvCreateImage(cvGetSize(frame), DEPTH, CHANNELS);
        cvCvtColor(frame, grayFrame, CV_BGR2GRAY);
        return grayFrame;
    }
    
    private CvSeq detectFaces(IplImage grayFrame) {
        double scaleFactor = 1.1;
        int minNeighbors = 3;
        
        return cvHaarDetectObjects(
                grayFrame,
                new CvHaarClassifierCascade(cvLoad(haarTemporaryLocation)),
                CvMemStorage.create(),
                scaleFactor,
                minNeighbors,
                CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH
        );
    }
    
    private static File createTemporaryJpgFile(byte[] data) {
        try {
            File file = File.createTempFile("facerec-tmp", ".jpg");
            FileUtils.writeByteArrayToFile(file, data);
            return file;
        } catch (Exception e) {
            throw new AssertionError("Could not create temp file", e);
        }
    }
}
