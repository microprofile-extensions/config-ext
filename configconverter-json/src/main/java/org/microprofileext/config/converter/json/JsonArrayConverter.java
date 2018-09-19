package org.microprofileext.config.converter.json;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.spi.Converter;

/**
 * Converts a json string to a JSonArray
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
public class JsonArrayConverter implements Converter<JsonArray> {

    @Override
    public JsonArray convert(String input) throws IllegalArgumentException {
        if(isNullOrEmpty(input))return Json.createArrayBuilder().build();
        
        try(JsonReader jsonReader = Json.createReader(new StringReader(input))){
            return jsonReader.readArray();
        }
    }
    
    /**
      * Not to sure about this, got an javax.json.stream.JsonParsingException in Wildfly with a value of org.eclipse.microprofile.config.configproperty.unconfigureddvalue
    **/
    private boolean isNullOrEmpty(String input){
        return input==null || input.isEmpty() || input.equals(UNCONFIGURED_VALUE);
    }
    
    private static final String UNCONFIGURED_VALUE = "org.eclipse.microprofile.config.configproperty.unconfigureddvalue";
}
