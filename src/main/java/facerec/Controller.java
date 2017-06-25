package facerec;

import com.google.gson.Gson;
import facerec.extract.ImageDto;
import facerec.recognize.SampleDto;
import org.apache.commons.configuration2.Configuration;
import spark.Spark;

import java.util.Base64;
import java.util.logging.Logger;

public class Controller {
    
    private Adapter adapter;
    private Logger log = Logger.getLogger(Controller.class.getName());
    private Configuration config;
    
    public Controller(Configuration config, Adapter adapter) {
        this.config = config;
        this.adapter = adapter;
    }
    
    public void setupWeb() {
        Spark.externalStaticFileLocation(config.getString("public_html"));
        Spark.secure(config.getString("cert_path"), "", null, "");
        Spark.port(config.getInt("port"));
        
        log.info("Setting up");
        
        Spark.before((req, response) -> log.info("Request: " + req.ip() + " " + req.url()));
        
        Spark.post("/recognize", (req, res) -> {
            SampleDto dto = new Gson().fromJson(req.body(), SampleDto.class);
            log.info("Recognizing, data: " + dto.data.substring(0, 10) + "...");
    
            return adapter.recognize(Base64.getDecoder().decode(dto.data));
        }, new JsonTransformer());
    
        Spark.post("/extractFace", (req, res) -> {
            ImageDto dto = new Gson().fromJson(req.body(), ImageDto.class);
            log.info("Extracting face, data: " + dto.data.substring(0, 10) + "...");
    
            return adapter.extractFace(Base64.getDecoder().decode(dto.data));
        }, new JsonTransformer());
        
        log.info("Routes all up");
    }
}
