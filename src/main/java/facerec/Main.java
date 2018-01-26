package facerec;

import facerec.core.FaceService;
import facerec.core.PredictionMappings;
import facerec.core.Trainer;
import facerec.web.Controller;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    
    private static final String CONFIG_FILENAME = "config.properties";
    private static final Logger log = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) throws Exception {
        log.info("Starting application...");
        
        PropertiesConfiguration config = loadConfiguration();
        
        String modelFilename = trainModel(config.getString("photos_dir"));
        config.setProperty("model_filename", modelFilename);
        
        PredictionMappings mappings = new PredictionMappings(config);
        
        FaceService faceService = new FaceService(config.getString("model_filename"), mappings);
        setupWeb(config, faceService);
        
        log.info("Startup finished");
    }
    
    private static void setupWeb(PropertiesConfiguration config, FaceService faceService) {
        String port = System.getenv("FACEREC_PORT");
        if (port == null) {
            port = config.getString("port");
        }
        
        log.info("App will be running on port " + config.getProperty("port"));
        Controller controller = new Controller(
                faceService,
                config.getString("public_html"),
                config.getString("cert_path"),
                Integer.parseInt(port)
        );
        
        controller.setupWeb();
    }
    
    private static String trainModel(String photosDirectory) {
        log.info("Training model...");
        String modelFilename = new Trainer(photosDirectory).train();
        log.info("Training finished, created model file: " + modelFilename);
        
        return modelFilename;
    }
    
    private static PropertiesConfiguration loadConfiguration() throws ConfigurationException {
        PropertiesConfiguration config;
        try {
            config = new Configurations().properties(new File(CONFIG_FILENAME));
        } catch (ConfigurationException e) {
            log.log(Level.SEVERE, "Error loading config file", e);
            throw e;
        }
        return config;
    }
}
