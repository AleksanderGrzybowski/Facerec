package facerec.dto;

import java.util.Base64;

public class FaceExtractionResultDto {
    public final String data;
    public final boolean success;
    
    private FaceExtractionResultDto(boolean success, String data) {
        this.success = success;
        this.data = data;
    }
    
    public static FaceExtractionResultDto failure() {
        return new FaceExtractionResultDto(false, null);
    }
    
    public static FaceExtractionResultDto success(byte[] data) {
        return new FaceExtractionResultDto(true, Base64.getEncoder().encodeToString(data));
    }
}
