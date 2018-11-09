package org.microprofileext.config.cdi;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * Making the Config sources available via CDI
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
@Dependent
public class ConfigSourceProvider {

    @Inject @ConfigSourceMap
    private Map<String,ConfigSource> configSourceMap;
    
    @Produces @Name("")
    public ConfigSource produceConfigSource(final InjectionPoint injectionPoint) {
        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        String name = getName(qualifiers);
        return configSourceMap.get(name);
    }
    
    private String getName(Set<Annotation> qualifiers){
        for(Annotation qualifier:qualifiers){
            if(qualifier.annotationType().equals(Name.class)){
                Name name = (Name)qualifier;
                return name.value();
            }
        }
        return "";
    }
}
