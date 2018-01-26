package facerec.web;

import com.google.gson.Gson;
import facerec.core.Adapter;
import facerec.dto.FaceExtrationImageDto;
import facerec.dto.RecognitionSampleDto;
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
        
        Spark.before((request, response) -> log.info("Request: " + request.ip() + " " + request.url()));
        
        Spark.post("/recognize", (request, response) -> {
            RecognitionSampleDto dto = new Gson().fromJson(request.body(), RecognitionSampleDto.class);
            log.info("Recognizing, data: " + dto.data.substring(0, 10) + "...");
            
            return adapter.recognize(Base64.getDecoder().decode(dto.data));
        }, new JsonTransformer());
        
        Spark.post("/extractFace", (request, response) -> {
            FaceExtrationImageDto dto = new Gson().fromJson(request.body(), FaceExtrationImageDto.class);
            log.info("Extracting face, data: " + dto.data.substring(0, 10) + "...");
            
            return adapter.extractFace(Base64.getDecoder().decode(dto.data));
        }, new JsonTransformer());
        
        log.info("Routes all up");
    }
}
