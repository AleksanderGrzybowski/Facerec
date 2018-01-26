package facerec.dto;

public class RecognitionStatusDto {
    public final boolean success;
    public final String prediction;
    
    private RecognitionStatusDto(boolean success, String prediction) {
        this.success = success;
        this.prediction = prediction;
    }
    
    public static RecognitionStatusDto success(String prediction) {
        return new RecognitionStatusDto(true, prediction);
    }
    
    public static RecognitionStatusDto failure() {
        return new RecognitionStatusDto(false, "failure");
    }
}
