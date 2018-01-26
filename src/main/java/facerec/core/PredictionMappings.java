package facerec.core;

import org.apache.commons.configuration2.PropertiesConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class PredictionMappings {
    
    private static final String MAPPING_PREFIX = "mapping_";
    
    private static final IntStream ID_RANGE = IntStream.rangeClosed(1, 10000);
    
    private final Map<Integer, String> mappings = new HashMap<>();
    
    public PredictionMappings(PropertiesConfiguration configuration) {
        ID_RANGE.forEach(id -> {
            String description = configuration.getString(MAPPING_PREFIX + id);
            if (description != null) {
                mappings.put(id, description);
            }
        });
    }
    
    public Optional<String> descriptionForValue(int predictionValue) {
        return Optional.ofNullable(mappings.get(predictionValue));
    }
}
