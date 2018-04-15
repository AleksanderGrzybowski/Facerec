package facerec.web;

import com.google.gson.Gson;
import facerec.core.FaceService;
import facerec.dto.FaceExtrationImageDto;
import facerec.dto.RecognitionSampleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import spark.ResponseTransformer;
import spark.Spark;

import java.util.Base64;

@Log
@RequiredArgsConstructor
public class Controller {
    
    private final FaceService faceService;
    private final String publicHtmlPath;
    private final String sslCertificatePath;
    private final int port;
    
    public void setupWeb() {
        Spark.externalStaticFileLocation(publicHtmlPath);
        if (sslCertificatePath != null) {
            Spark.secure(sslCertificatePath, "", null, "");
        }
        Spark.port(port);
        
        log.info("Setting up...");
        
        Spark.before((request, response) -> log.info("Request: " + request.ip() + " " + request.url()));
        
        ResponseTransformer jsonTransformer = createJsonTransformer();
        
        Spark.post("/recognize", (request, response) -> {
            RecognitionSampleDto dto = new Gson().fromJson(request.body(), RecognitionSampleDto.class);
            log.info("Recognizing, data: " + dto.data.substring(0, 10) + "...");
            
            return faceService.recognize(Base64.getDecoder().decode(dto.data));
        }, jsonTransformer);
        
        Spark.post("/extractFace", (request, response) -> {
            FaceExtrationImageDto dto = new Gson().fromJson(request.body(), FaceExtrationImageDto.class);
            log.info("Extracting face, data: " + dto.data.substring(0, 10) + "...");
            
            return faceService.extractFace(Base64.getDecoder().decode(dto.data));
        }, jsonTransformer);
    
        Spark.get("/health", ((request, response) -> new HealthcheckOkDto()) , createJsonTransformer());
        
        log.info("Routes all up.");
    }
    
    private ResponseTransformer createJsonTransformer() {
        Gson gson = new Gson();
        return gson::toJson;
    }
    
    @SuppressWarnings("unused")
    private class HealthcheckOkDto {
        public final String status = "OK";
    }
}
