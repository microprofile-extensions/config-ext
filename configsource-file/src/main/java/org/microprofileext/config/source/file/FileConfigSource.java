package org.microprofileext.config.source.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * File config source
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 * @author <a href="mailto:dpmoore@acm.org">Derek P. Moore</a>
 */
@Log
@NoArgsConstructor
public class FileConfigSource implements ConfigSource {
    
    public static final String NAME = "FileConfigSource";

    private static final String KEY_PREFIX = "configsource.file.";

    private static final String KEY_ENABLED = KEY_PREFIX + "enabled";
    private static final boolean DEFAULT_ENABLED = false;
    
    private static final String KEY_FILE_URI = KEY_PREFIX + "fileuri";
    private static final String DEFAULT_FILE_URI = "application.properties";

    private Map<String,String> map;

    @Override
    public int getOrdinal() {
        return 350;
    }
    
    @Override
    public Map<String, String> getProperties() {
        if(isEnabled())return this.getMap();
        return new HashMap<>();
    }

    @Override
    public String getValue(String key) {
        if (key.startsWith(KEY_PREFIX)) {
            // in case we are about to configure ourselves we simply ignore that key
            return null;
        }
        if(isEnabled()){
            return getMap().get(key);
        }
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }
    
    private boolean isEnabled(){
        Config cfg = ConfigProvider.getConfig();
        return cfg.getOptionalValue(KEY_ENABLED, Boolean.class).orElse(DEFAULT_ENABLED);
    }
    
    private Map<String,String> getMap(){
        if(this.map == null ){
            this.map = new HashMap<>();
            Config cfg = ConfigProvider.getConfig();
            String fileuri = cfg.getOptionalValue(KEY_FILE_URI, String.class).orElse(DEFAULT_FILE_URI);
            try {
                log.info("Loading [file] MicroProfile ConfigSource");
                Properties p = new Properties();
                p.load(new FileInputStream(fileuri));
                
                for (final String name: p.stringPropertyNames()){
                    this.map.put(name, p.getProperty(name));
                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Can not load properties file [{0}] due to [{1}]", new Object[]{fileuri, ex.getMessage()});
            } 
        }
        return this.map;
    }
    

}
