package facerec;

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
        
        PropertiesConfiguration config;
        try {
            config = new Configurations().properties(new File(CONFIG_FILENAME));
        } catch (ConfigurationException e) {
            log.log(Level.SEVERE, "Error loading config file", e);
            throw e;
        }
        
        log.info("Training model...");
        Trainer trainer = new Trainer(config);
        String modelFilename = trainer.train();
        log.info("Training finished, created model file: " + modelFilename);
        config.setProperty("model_filename", modelFilename);
        
        
        String envPort = System.getenv("FACEREC_PORT");
        if (envPort != null) {
            config.setProperty("port", Integer.parseInt(envPort));
        }
        log.info("App will be running on port " + config.getProperty("port"));
        
        Adapter adapter = new Adapter(config);
        Controller controller = new Controller(config, adapter);
        
        controller.setupWeb();
        log.info("Startup finished");
    }
}
