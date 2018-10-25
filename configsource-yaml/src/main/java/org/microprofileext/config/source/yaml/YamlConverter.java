package org.microprofileext.config.source.yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;

import org.yaml.snakeyaml.Yaml;

/**
 * Convert yaml format to Map
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
public class YamlConverter {
    
    @Getter
    private final Map<String,String> properties = new TreeMap<>();
    
    public YamlConverter(InputStream in){
        Yaml yaml = new Yaml();
        TreeMap<String, Map<String, Object>> yamlInput = yaml.loadAs(in, TreeMap.class);
        for (String key : yamlInput.keySet()) {
            populateMap(key, yamlInput.get(key));
        }
    }

    private void populateMap(String key, Map<String, Object> map) {
        for (String mapKey : map.keySet()) {
            populateEntry(key,mapKey,map);
        }

    }
    
    private void populateEntry(String key, String mapKey, Map<String, Object> map){
        if (map.get(mapKey) instanceof Map) {
            populateMap(String.format(FORMAT, key, mapKey), (Map<String, Object>) map.get(mapKey));
        } else {
            properties.put(String.format(FORMAT, key, mapKey), map.get(mapKey).toString());
        }   
    }
    
    private static final String FORMAT = "%s.%s"; // TODO: Allow this to be configured ?
}