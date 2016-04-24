package facerec;

import com.google.gson.Gson;
import spark.Spark;

import java.io.File;
import java.util.Base64;

public class Controller {
    
    private static final String CERT_PATH = "ssl.p12";
    private static final String PUBLIC_HTML = System.getProperty("user.dir") + File.separator + "public";
    
    public static void main(String[] args) {
        Spark.externalStaticFileLocation(PUBLIC_HTML);
        Spark.secure(CERT_PATH, "", null, "");
        
        Spark.post("/recognize", (req, res) -> {
            PhotoDto dto = new Gson().fromJson(req.body(), PhotoDto.class);
    
            byte[] bytes = Base64.getDecoder().decode(dto.data);
            
            return Adapter.recognize(bytes);
        }, new JsonTransformer());
    }
}
