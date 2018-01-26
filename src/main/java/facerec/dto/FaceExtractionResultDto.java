package facerec.dto;

import lombok.RequiredArgsConstructor;

import java.util.Base64;

@RequiredArgsConstructor
public class FaceExtractionResultDto {
    public final boolean success;
    public final String data;
    
    public static FaceExtractionResultDto failure() {
        return new FaceExtractionResultDto(false, null);
    }
    
    public static FaceExtractionResultDto success(byte[] data) {
        return new FaceExtractionResultDto(true, Base64.getEncoder().encodeToString(data));
    }
}
