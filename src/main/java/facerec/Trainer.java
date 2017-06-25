package facerec;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

/**
 * This is ugly as hell, but at least tries to mimick original C++ code.
 * https://github.com/bytedeco/javacv/blob/master/samples/OpenCVFaceRecognizer.java
 */
public class Trainer {
    
    private PropertiesConfiguration config;
    private Logger log = Logger.getLogger(Trainer.class.getName());
    
    public Trainer(PropertiesConfiguration config) {
        this.config = config;
    }
    
    @SuppressWarnings("ConstantConditions")
    public String train() {
        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        
        String photosFolderName = config.getString("photos_dir");
        File photosFolder = new File(photosFolderName);
        if (!photosFolder.isDirectory()) {
            throw new RuntimeException("Photos folder not found, should be: " + photosFolderName);
        }
    
        Arrays.stream(photosFolder.listFiles())
                .filter(File::isDirectory)
                .forEach(directory -> {
                    log.info("Traversing directory " + directory.getName());
                    
                    for (File photo : directory.listFiles()) {
                        log.info("Loading image " + photo.getName());
                        images.add(imread(photo.getAbsolutePath(), 0));
                        labels.add(Integer.parseInt(directory.getName()));
                    }
                });
        
        
        Mat labelsMat = prepareLabels(labels);
        MatVector matVector = new MatVector(images.toArray(new Mat[0]));
        
        FaceRecognizer model = createEigenFaceRecognizer();
        model.train(matVector, labelsMat);
    
        try {
            File modelFile = File.createTempFile("model", ".yml");
            String modelPath = modelFile.getAbsolutePath();
            
            model.save(modelPath);
            log.info("Model saved to " + modelPath);
            return modelPath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    // this sucks even more
    private Mat prepareLabels(List<Integer> labels) {
        Mat labelsMat = new Mat(labels.size(), 1, CV_32SC1);
        IntBuffer buffer = labelsMat.createBuffer();
        
        for (int i = 0; i < labels.size(); ++i) {
            buffer.put(i, labels.get(i));
        }
        
        return labelsMat;
    }
}
