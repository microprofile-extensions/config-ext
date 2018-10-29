package org.microprofileext.config.source.base;

import java.io.IOException;
import java.io.InputStream;
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
    protected final String keySeparator;
    
    public AbstractUrlBasedSource(){
        log.log(Level.INFO, "Loading [{0}] MicroProfile ConfigSource", getFileExtension());
        this.keySeparator = loadPropertyKeySeparator();
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
    
    private Map<String, String> loadUrl(String url) {
        log.log(Level.INFO, "Using [{0}] as {1} URL", new Object[]{url, getFileExtension()});
        Map<String,String> map = new HashMap<>();
        
        URL u;
        InputStream inputStream = null;
        
        try {
            u = new URL(url);
            inputStream = u.openStream();
            if (inputStream != null) {
                map = toMap(inputStream);
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Unable to read URL [{0}] - {1}", new Object[]{url, e.getMessage()});
        } finally {
            try {
                if (inputStream != null)inputStream.close();
            // no worries, means that the file is already closed
            } catch (IOException e) {}
        }
        return map;
    }
    
    private String getConfigKey(String subKey){
        return getPrefix() + subKey;
    }
    
    private String loadPropertyKeySeparator(){
        Config cfg = ConfigProvider.getConfig();
        return cfg.getOptionalValue(getConfigKey(KEY_SEPARATOR), String.class).orElse(DOT);
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
    private static final String KEY_SEPARATOR = "keyseparator";
    private static final String CONFIGSOURCE = "configsource";
    private static final String APPLICATION = "application";
    
    protected abstract String getFileExtension();
    protected abstract Map<String,String> toMap(final InputStream inputStream);
    
}
