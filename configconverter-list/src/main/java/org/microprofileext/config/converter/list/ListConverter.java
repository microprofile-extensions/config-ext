package org.microprofileext.config.converter.list;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.microprofile.config.spi.Converter;

/**
 * Converts a comma separated string to a list
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
public class ListConverter implements Converter<List> {

    @Override
    public List convert(String input) throws IllegalArgumentException {
        if(isNullOrEmpty(input))return new ArrayList();
        
        Stream<String> stream = Stream.of(input);
        return stream.collect(Collectors.toList());
    }
    
    /**
      * Not to sure about this, got an javax.json.stream.JsonParsingException in Wildfly with a value of org.eclipse.microprofile.config.configproperty.unconfigureddvalue
    **/
    private boolean isNullOrEmpty(String input){
        return input==null || input.isEmpty() || input.equals(UNCONFIGURED_VALUE);
    }
    
    private static final String UNCONFIGURED_VALUE = "org.eclipse.microprofile.config.configproperty.unconfigureddvalue";
}