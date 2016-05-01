package facerec.recognize;

public class RecoStatusDto {
    public final boolean success;
    public final int prediction;
    
    private RecoStatusDto(boolean success, int prediction) {
        this.success = success;
        this.prediction = prediction;
    }
    
    public static RecoStatusDto success(int prediction) {
        return new RecoStatusDto(true, prediction);
    }
    
    public static RecoStatusDto failure() {
        return new RecoStatusDto(false, 0);
    }
}
