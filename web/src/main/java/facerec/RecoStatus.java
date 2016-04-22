package facerec;

public class RecoStatus {
    public final boolean success;
    public final int prediction;
    
    private RecoStatus(boolean success, int prediction) {
        this.success = success;
        this.prediction = prediction;
    }
    
    public static RecoStatus success(int prediction) {
        return new RecoStatus(true, prediction);
    }
    
    public static RecoStatus failure() {
        return new RecoStatus(false, 0);
    }
}
