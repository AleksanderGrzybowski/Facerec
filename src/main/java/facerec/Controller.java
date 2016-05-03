package facerec;

import com.google.gson.Gson;
import facerec.extract.ImageDto;
import facerec.recognize.SampleDto;
import spark.Spark;

import java.util.Base64;
import java.util.logging.Logger;

public class Controller {
    
    private static Logger log = Logger.getLogger(Controller.class.getName());
    
    public static void main(String[] args) {
        Spark.externalStaticFileLocation(Config.PUBLIC_HTML);
        Spark.secure(Config.CERT_PATH, "", null, "");
        Spark.port(Config.PORT);
        
        log.info("Setting up");
        
        Spark.before((req, response) -> log.info(req.ip() + " " + req.url()));
        
        Spark.post("/recognize", (req, res) -> {
            SampleDto dto = new Gson().fromJson(req.body(), SampleDto.class);
    
            return Adapter.recognize(Base64.getDecoder().decode(dto.data), dto.method);
        }, new JsonTransformer());
    
        Spark.post("/extractFace", (req, res) -> {
            ImageDto dto = new Gson().fromJson(req.body(), ImageDto.class);
    
            return Adapter.extractFace(Base64.getDecoder().decode(dto.data));
        }, new JsonTransformer());
    }
}
