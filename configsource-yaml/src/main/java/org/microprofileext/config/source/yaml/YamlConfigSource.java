package org.microprofileext.config.source.yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import lombok.extern.java.Log;
import org.microprofileext.config.source.base.AbstractUrlBasedSource;
import org.yaml.snakeyaml.Yaml;

/**
 * Yaml config source
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
public class YamlConfigSource extends AbstractUrlBasedSource {

    @Override
    protected String getFileExtension() {
        return "yaml";
    }

    @Override
    protected Map<String, String> toMap(InputStream inputStream) {
        final Map<String,String> properties = new TreeMap<>();
        Yaml yaml = new Yaml();
        TreeMap<String, Object> yamlInput = yaml.loadAs(inputStream, TreeMap.class);
        
        for (String key : yamlInput.keySet()) {
            populateMap(properties,key, yamlInput.get(key));
        }
        return properties;
    }
    
    private void populateMap(Map<String,String> properties, String key, Object o) {
        
        if (o instanceof Map) {
            Map map = (Map)o;
            for (Object mapKey : map.keySet()) {
                populateEntry(properties, key,mapKey.toString(),map);
            }
        }else{
            if(o!=null)properties.put(key,o.toString());
        }
    }
    
    private void populateEntry(Map<String,String> properties, String key, String mapKey, Map<String, Object> map){
        String format = "%s" + keySeparator + "%s";
        if (map.get(mapKey) instanceof Map) {
            populateMap(properties, String.format(format, key, mapKey), (Map<String, Object>) map.get(mapKey));
        } else {
            properties.put(String.format(format, key, mapKey), map.get(mapKey).toString());
        }   
    }
}