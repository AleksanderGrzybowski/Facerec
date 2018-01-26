package facerec.web;

import com.google.gson.Gson;
import spark.ResponseTransformer;

/**
 * I would be glad if his nice guy behind Spark built this in...
 */
public class JsonTransformer implements ResponseTransformer {
    
    private Gson gson = new Gson();
    
    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
