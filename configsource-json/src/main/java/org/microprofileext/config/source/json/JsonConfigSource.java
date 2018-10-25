package org.microprofileext.config.source.json;

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
import org.microprofileext.config.source.base.EnabledConfigSource;

/**
 * Json config source
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
public class JsonConfigSource extends EnabledConfigSource {

    private static final String KEY_PREFIX = "configsource.json.";
    
    private static final String KEY_URL = KEY_PREFIX + "url";
    private static final String DEFAULT_URL = "application.json";

    private final Map<String, String> properties;
    private final String jsonUrl;
    
    public JsonConfigSource(){
        log.info("Loading [json] MicroProfile ConfigSource");
        this.jsonUrl = loadJsonFileUrlPath();
        this.properties = loadJsons(jsonUrl);
        super.initOrdinal(500);
    }
    
    @Override
    public Map<String, String> getPropertiesIfEnabled() {
        return this.properties;
    }

    @Override
    public String getValue(String key) {
        // in case we are about to configure ourselves we simply ignore that key
        if(super.isEnabled() && !key.startsWith(KEY_PREFIX)){
            return this.properties.get(key);
        }
        return null;
    }

    @Override
    public String getName() {
        return CLASS_KEY_PREFIX + UNDERSCORE + this.jsonUrl;
    }
    
    private Map<String, String> loadJsons(String surl) {
        Map<String,String> map = new HashMap<>();
        String urls[] = surl.split(COMMA);
        
        for(String url:urls){
            if(url!=null && !url.isEmpty()){
                map.putAll(loadJson(url.trim()));
            }
        }
        return map;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, String> loadJson(String url) {
        log.log(Level.INFO, "Using [{0}] as yaml URL", url);
        Map<String,String> map = new HashMap<>();
        
        URL u;
        InputStream inputStream = null;
        
        try {
            u = new URL(url);
            inputStream = u.openStream();
            if (inputStream != null) {
                JsonConverter jsonConverter = new JsonConverter(inputStream);
                map = jsonConverter.getProperties();
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
    
    private String getDefaultURL(String path){
        try {
            URL url = Paths.get(path).toUri().toURL();
            return url.toString();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private String loadJsonFileUrlPath(){
        Config cfg = ConfigProvider.getConfig();
        String url = cfg.getOptionalValue(KEY_URL, String.class).orElse(getDefaultURL(DEFAULT_URL));
        
        return url;
    }
    
    private static final String UNDERSCORE = "_";   
    private static final String COMMA = ",";
}