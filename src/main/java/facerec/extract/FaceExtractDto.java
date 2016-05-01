package facerec.extract;

import java.util.Base64;

public class FaceExtractDto {
    public final String data;
    public final boolean success;
    
    private FaceExtractDto(boolean success, String data) {
        this.success = success;
        this.data = data;
    }
    
    public static FaceExtractDto failure() {
        return new FaceExtractDto(false, null);
    }
    
    public static FaceExtractDto success(byte[] data) {
        return new FaceExtractDto(true, Base64.getEncoder().encodeToString(data));
    }
}
