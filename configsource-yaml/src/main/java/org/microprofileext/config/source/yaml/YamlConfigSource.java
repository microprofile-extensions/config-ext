package org.microprofileext.config.source.yaml;

import java.io.InputStream;
import java.util.*;

import lombok.extern.java.Log;
import org.microprofileext.config.source.base.file.AbstractUrlBasedSource;
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
    @SuppressWarnings("unchecked")
    protected Map<String, String> toMap(InputStream inputStream) {
        final Map<String,String> properties = new TreeMap<>();
        Yaml yaml = new Yaml();
        Map<String, Object> yamlInput = yaml.loadAs(inputStream, TreeMap.class);

        if(Objects.nonNull(yamlInput)) {
            for (String key : yamlInput.keySet()) {
                populateMap(properties,key, yamlInput.get(key));
            }
        }
        return properties;
    }
    
    @SuppressWarnings("unchecked")
    private void populateMap(Map<String,String> properties, String key, Object o) {
        if (o instanceof Map) {
            Map map = (Map)o;
            for (Object mapKey : map.keySet()) {
                populateEntry(properties, key,mapKey.toString(),map);
            }
        } else if (o instanceof List) {
            List<String> l = toStringList((List)o);
            properties.put(key,String.join(COMMA, l));
        } else{
            if(o!=null)properties.put(key,o.toString());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void populateEntry(Map<String,String> properties, String key, String mapKey, Map<String, Object> map){
        String format = "%s" + super.getKeySeparator() + "%s";
        if (map.get(mapKey) instanceof Map) {
            populateMap(properties, String.format(format, key, mapKey), (Map<String, Object>) map.get(mapKey));
        } else if (map.get(mapKey) instanceof List) {
            List<String> l = toStringList((List)map.get(mapKey));
            properties.put(String.format(format, key, mapKey),String.join(COMMA, l));
        } else {
            properties.put(String.format(format, key, mapKey), map.get(mapKey).toString());
        }   
    }
    
    private List<String> toStringList(List l){
        List<String> nl = new ArrayList<>();
        for(Object o:l){
            String s = String.valueOf(o);
            if(s.contains(COMMA))s = s.replaceAll(COMMA, "\\\\,"); // Escape comma
            nl.add(s);
        }
        return nl;
    }
    
    private static final String COMMA = ",";
}