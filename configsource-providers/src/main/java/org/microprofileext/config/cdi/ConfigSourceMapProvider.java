package org.microprofileext.config.cdi;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * Making the Config sources available via CDI
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@ApplicationScoped
public class ConfigSourceMapProvider {
    
    private static final Logger log = Logger.getLogger(ConfigSourceMapProvider.class.getName());
    
    @Inject
    private Provider<Config> configProvider;
    
    private final Map<String,ConfigSource> configSourceMap = new HashMap<>();
    
    @Produces @ConfigSourceMap
    public Map<String,ConfigSource> produceConfigSourceMap(){
        if(this.configSourceMap.isEmpty()){
            for(ConfigSource configSource:configProvider.get().getConfigSources()){
                this.configSourceMap.put(configSource.getName(), configSource);
            }
        }
        return this.configSourceMap;
    }
}
