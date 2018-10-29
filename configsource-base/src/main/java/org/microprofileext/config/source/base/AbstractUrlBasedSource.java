package org.microprofileext.config.source.base;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * URL Based property files
 * 
 * Load some file from a file and convert to properties.
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
public abstract class AbstractUrlBasedSource extends EnabledConfigSource {
    
    private final Map<String, String> properties;
    private final String url;
    
    public AbstractUrlBasedSource(){
        log.log(Level.INFO, "Loading [{0}] MicroProfile ConfigSource", getFileExtension());
        this.url = loadUrlPath();
        this.properties = loadUrls(url);
        super.initOrdinal(500);
    }
    
    @Override
    public Map<String, String> getPropertiesIfEnabled() {
        return this.properties;
    }

    @Override
    public String getValue(String key) {
        // in case we are about to configure ourselves we simply ignore that key
        if(super.isEnabled() && !key.startsWith(getPrefix())){
            return this.properties.get(key);
        }
        return null;
    }

    @Override
    public String getName() {
        return CLASS_KEY_PREFIX + UNDERSCORE + this.url;
    }
    
    protected String getConfigKey(String subKey){
        return getPrefix() + subKey;
    }
    
    private Map<String, String> loadUrls(String surl) {
        Map<String,String> map = new HashMap<>();
        String urls[] = surl.split(COMMA);
        
        for(String u:urls){
            if(u!=null && !u.isEmpty()){
                map.putAll(loadUrl(u.trim()));
            }
        }
        return map;
    }
    
    private String loadUrlPath(){
        Config cfg = ConfigProvider.getConfig();
        return cfg.getOptionalValue(getConfigKey(URL), String.class).orElse(getDefaultUrl());
    }
    
    private String getPrefix(){
        return CONFIGSOURCE + DOT + getFileExtension() + DOT;
    }
    
    private String getDefaultUrl(){
        String path = APPLICATION + DOT + getFileExtension();
        try {
            URL u = Paths.get(path).toUri().toURL();
            return u.toString();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static final String COMMA = ",";
    private static final String UNDERSCORE = "_";
    private static final String DOT = ".";
    private static final String URL = "url";
    private static final String CONFIGSOURCE = "configsource";
    private static final String APPLICATION = "application";
    
    protected abstract String getFileExtension();
    protected abstract Map<String, String> loadUrl(String url);
    
}
