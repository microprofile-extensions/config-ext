package org.microprofileext.config.source.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.microprofileext.config.source.base.EnabledConfigSource;

/**
 * File config source
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 * @author <a href="mailto:dpmoore@acm.org">Derek P. Moore</a>
 */
public class FileConfigSource extends EnabledConfigSource {

    private final String KEY_FILE_URI = CLASS_KEY_PREFIX + "fileuri";
    private static final String DEFAULT_FILE_URI = "application.properties";

    private Map<String,String> map;
    String fileuri;

    public FileConfigSource() {
        initOrdinal(350);
    }

    @Override
    public Map<String, String> getPropertiesIfEnabled() {
        return this.getMap();
    }

    @Override
    public String getValue(String key) {
        if (
                key.startsWith(CLASS_KEY_PREFIX)
                || key.startsWith(INSTANCE_KEY_PREFIX)
                || key.equals("config_ordinal")
        ) {
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
        return CLASS_KEY_PREFIX + (fileuri != null ? "." + fileuri : "");
    }
    
    private Map<String,String> getMap(){
        if(this.map == null ){
            this.map = new HashMap<>();
            Config cfg = ConfigProvider.getConfig();
            fileuri = cfg.getOptionalValue(KEY_FILE_URI, String.class).orElse(DEFAULT_FILE_URI);
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
