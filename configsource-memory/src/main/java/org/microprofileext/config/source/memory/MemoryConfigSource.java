package org.microprofileext.config.source.memory;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.java.Log;
import org.microprofileext.config.source.base.EnabledConfigSource;

/**
 * In memory config source. Use the REST Endpoint to populate values
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
public class MemoryConfigSource extends EnabledConfigSource {
    
    public static final String NAME = "MemoryConfigSource";
    private static final Map<String,String> PROPERTIES = new HashMap<>();
    
    public MemoryConfigSource(){
        log.info("Loading [memory] MicroProfile ConfigSource");
        initOrdinal(900);
    }
    
    
    @Override
    public Map<String, String> getPropertiesIfEnabled() {
        return PROPERTIES;
    }

    @Override
    public String getValue(String key) {
        if(PROPERTIES.containsKey(key)){
            return PROPERTIES.get(key);
        }
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }
}