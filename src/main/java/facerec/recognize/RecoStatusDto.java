package facerec.recognize;

public class RecoStatusDto {
    public final boolean success;
    public final String prediction;
    
    private RecoStatusDto(boolean success, String prediction) {
        this.success = success;
        this.prediction = prediction;
    }
    
    public static RecoStatusDto success(String prediction) {
        return new RecoStatusDto(true, prediction);
    }
    
    public static RecoStatusDto failure() {
        return new RecoStatusDto(false, "failure");
    }
}
