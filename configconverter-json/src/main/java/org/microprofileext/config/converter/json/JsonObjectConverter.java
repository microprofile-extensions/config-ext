package org.microprofileext.config.converter.json;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.eclipse.microprofile.config.spi.Converter;

/**
 * Converts a json string to a JSonObject
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
public class JsonObjectConverter implements Converter<JsonObject> {

    @Override
    public JsonObject convert(String input) throws IllegalArgumentException {
        if(isNullOrEmpty(input))return Json.createObjectBuilder().build();
        
        try(JsonReader jsonReader = Json.createReader(new StringReader(input))){
            return jsonReader.readObject();
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
