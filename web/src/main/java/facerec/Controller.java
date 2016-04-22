package facerec;

import com.google.gson.Gson;
import spark.Spark;

import java.io.File;
import java.util.Base64;

public class Controller {
    public static void main(String[] args) {
        Spark.externalStaticFileLocation(System.getProperty("user.dir") + File.separator + "public");
        
        Spark.post("/recognize", (req, res) -> {
            PhotoDto dto = new Gson().fromJson(req.body(), PhotoDto.class);
    
            byte[] bytes = Base64.getDecoder().decode(dto.data);
            
            return Adapter.recognize(bytes);
        }, new JsonTransformer());
    }
}
