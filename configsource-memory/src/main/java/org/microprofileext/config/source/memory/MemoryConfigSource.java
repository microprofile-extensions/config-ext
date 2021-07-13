package org.microprofileext.config.source.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.microprofileext.config.source.base.EnabledConfigSource;

/**
 * In memory config source. Use the REST Endpoint to populate values
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
public class MemoryConfigSource extends EnabledConfigSource {
    
    private static final Logger log = Logger.getLogger(MemoryConfigSource.class.getName());
    
    public static final String NAME = "MemoryConfigSource";
    private static final Map<String,String> PROPERTIES = new HashMap<>();
    
    public MemoryConfigSource(){
        log.info("Loading [memory] MicroProfile ConfigSource");
        super.initOrdinal(900);
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