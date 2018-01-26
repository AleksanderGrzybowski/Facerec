package facerec.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RecognitionStatusDto {
    public final boolean success;
    public final String prediction;
    
    public static RecognitionStatusDto success(String prediction) {
        return new RecognitionStatusDto(true, prediction);
    }
    
    public static RecognitionStatusDto failure() {
        return new RecognitionStatusDto(false, "failure");
    }
}
