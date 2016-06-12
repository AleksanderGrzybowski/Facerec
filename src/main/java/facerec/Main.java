package facerec;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    
    private static Logger log = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) throws Exception {
        Configurations configs = new Configurations();
        File propertiesFile = new File("config.properties");
        
        PropertiesConfiguration config = null;
        try {
            config = configs.properties(propertiesFile);
        } catch (ConfigurationException e) {
            log.log(Level.SEVERE, "Error loading config file", e);
        }
    
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            config.setProperty("port", Integer.parseInt(envPort));
        }
        
        Adapter adapter = new Adapter(config);
        Controller controller = new Controller(config, adapter);
        
        controller.setupWeb();
    }
}
