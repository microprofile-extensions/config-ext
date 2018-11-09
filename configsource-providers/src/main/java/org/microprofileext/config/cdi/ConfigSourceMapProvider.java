package org.microprofileext.config.cdi;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * Making the Config sources available via CDI
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
@ApplicationScoped
public class ConfigSourceMapProvider {
    @Inject
    private Config config;
    
    @Produces @ConfigSourceMap
    private final Map<String,ConfigSource> configSourceMap = new HashMap<>();
    
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        for(ConfigSource configSource:config.getConfigSources()){
            this.configSourceMap.put(configSource.getName(), configSource);
        }
    }
 
    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init) {
        configSourceMap.clear();
    }
}
