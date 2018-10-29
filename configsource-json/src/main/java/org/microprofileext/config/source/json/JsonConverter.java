package org.microprofileext.config.source.json;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * Convert json format to Map
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
public class JsonConverter {
    
    @Getter
    private final Map<String,String> properties = new TreeMap<>();
    
    public JsonConverter(InputStream in){
        try (JsonReader reader = Json.createReader(in)) {
            Map<String, Object> jsonMap = jsonToMap(reader.readObject());
            
            for (String key : jsonMap.keySet()) {
                populateMap(key, jsonMap.get(key));
            }
        }
    }

    private void populateMap(String key, Object o) {
        
        if (o instanceof Map) {
            Map map = (Map)o;
            for (Object mapKey : map.keySet()) {
                populateEntry(key,mapKey.toString(),map);
            }
        }else{
            if(o!=null)properties.put(key,o.toString());
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


    private Map<String, Object> jsonToMap(JsonObject json) {
        Map<String, Object> retMap = new HashMap<>();

        if(json != JsonObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    private Object toObject(String key,JsonObject object){
        JsonValue.ValueType valueType = object.getValueType();
            
        if(valueType.equals(JsonValue.ValueType.ARRAY)) {
            return toList(object.getJsonArray(key));
        }else if (valueType.equals(JsonValue.ValueType.OBJECT)) {
            JsonValue jsonValue = object.get(key);
            JsonValue.ValueType objectType = jsonValue.getValueType();

            if(objectType.equals(JsonValue.ValueType.ARRAY)) {
                return toList(object.getJsonArray(key));
            }else if(objectType.equals(JsonValue.ValueType.STRING)){
                return object.getString(key);
            }else if(objectType.equals(JsonValue.ValueType.NUMBER)){
                return object.getInt(key);
            }else if(objectType.equals(JsonValue.ValueType.FALSE)){
                return false;
            }else if(objectType.equals(JsonValue.ValueType.TRUE)){
                return true;
            }else if(objectType.equals(JsonValue.ValueType.NULL)){
                return null;
            }else if(objectType.equals(JsonValue.ValueType.OBJECT)) {
                return toMap(object.getJsonObject(key));
            }else{
                return object.get(key);
            }
        }else{
            return object.get(key);
        }
    }
    
    private List<Object> toList(JsonArray array) {
        List<Object> list = new ArrayList<>();
        for(int i = 0; i < array.size(); i++) {
            
            
            Object value = array.get(i);
            if(value instanceof JsonArray) {
                value = toList((JsonArray) value);
            }else if(value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            }else{
                JsonValue jv = array.get(i);
                JsonValue.ValueType objectType = jv.getValueType();
                
                if(objectType.equals(JsonValue.ValueType.STRING)){
                    value = array.getString(i);
                }else if(objectType.equals(JsonValue.ValueType.NUMBER)){
                    value = array.getInt(i);
                }else if(objectType.equals(JsonValue.ValueType.FALSE)){
                    value = false;
                }else if(objectType.equals(JsonValue.ValueType.TRUE)){
                    value = true;
                }else if(objectType.equals(JsonValue.ValueType.NULL)){
                    value = null;
                }
            }
            list.add(value);
        }
        return list;
    }
    
    private Map<String, Object> toMap(JsonObject object) throws JsonException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keySet().iterator();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = toObject(key,object);
            map.put(key, value);
        }
        return map;
    }
    
}