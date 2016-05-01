package facerec;

public class FaceExtractDto {
    public final String data;
    public final String status;
    
    private FaceExtractDto(String data, String status) {
        this.data = data;
        this.status = status;
    }
    
    public static FaceExtractDto failure() {
        return new FaceExtractDto(null, "failure");
    }
    
    public static FaceExtractDto success(String base64data) {
        return new FaceExtractDto(null, base64data);
    }
}
