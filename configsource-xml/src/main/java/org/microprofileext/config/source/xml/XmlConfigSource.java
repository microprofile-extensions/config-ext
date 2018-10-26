package org.microprofileext.config.source.xml;

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
 * Xml config source
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
public class XmlConfigSource extends EnabledConfigSource {

    private static final String KEY_PREFIX = "configsource.xml.";
    
    private static final String KEY_URL = KEY_PREFIX + "url";
    private static final String DEFAULT_URL = "application.xml";

    private static final String KEY_IGNORE_ROOT = KEY_PREFIX + "ignoreRoot";
    private static final boolean DEFAULT_IGNORE_ROOT = true;
    
    private final Map<String, String> properties;
    private final String xmlUrl;
    private boolean ignoreRoot = true;
    
    public XmlConfigSource(){
        log.info("Loading [xml] MicroProfile ConfigSource");
        this.xmlUrl = loadXmlFileUrlPath();
        this.ignoreRoot = loadIgnoreRoot();
        this.properties = loadXmls(xmlUrl,ignoreRoot);
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
        return CLASS_KEY_PREFIX + UNDERSCORE + this.xmlUrl;
    }
    
    private Map<String, String> loadXmls(String surl,boolean ignoreRoot) {
        Map<String,String> map = new HashMap<>();
        String urls[] = surl.split(COMMA);
        
        for(String url:urls){
            if(url!=null && !url.isEmpty()){
                map.putAll(loadXml(url.trim(),ignoreRoot));
            }
        }
        return map;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, String> loadXml(String url,boolean ignoreRoot) {
        log.log(Level.INFO, "Using [{0}] as xml URL", url);
        Map<String,String> map = new HashMap<>();
        
        URL u;
        InputStream inputStream = null;
        
        try {
            u = new URL(url);
            inputStream = u.openStream();
            if (inputStream != null) {
                XmlConverter yamlConverter = new XmlConverter(inputStream,ignoreRoot);
                map = yamlConverter.getProperties();
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
    
    private String loadXmlFileUrlPath(){
        Config cfg = ConfigProvider.getConfig();
        String url = cfg.getOptionalValue(KEY_URL, String.class).orElse(getDefaultURL(DEFAULT_URL));
        
        return url;
    }
    
    private boolean loadIgnoreRoot(){
        Config cfg = ConfigProvider.getConfig();
        return cfg.getOptionalValue(KEY_IGNORE_ROOT, Boolean.class).orElse(DEFAULT_IGNORE_ROOT);
    }
    
    private static final String UNDERSCORE = "_";   
    private static final String COMMA = ",";
}