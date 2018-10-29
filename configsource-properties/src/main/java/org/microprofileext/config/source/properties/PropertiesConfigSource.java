// TODO FIXME directly including a Geronimo Config class due to inaccessibility
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.microprofileext.config.source.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import org.microprofileext.config.source.base.EnabledConfigSource;

/**
 * Properties config source
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 * @author <a href="mailto:dpmoore@acm.org">Derek P. Moore</a>
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
public class PropertiesConfigSource extends EnabledConfigSource {
    
    private static final String KEY_PREFIX = "configsource.properties.";
    
    private static final String KEY_URL = KEY_PREFIX + "url";
    private static final String DEFAULT_URL = "application.properties";

    private final Map<String, String> properties;
    private final String fileUrl;
    
    public PropertiesConfigSource(){
        log.info("Loading [properties] MicroProfile ConfigSource");
        this.fileUrl = loadPropertiesFileUrlPath();
        this.properties = loadProperties(fileUrl);
        super.initOrdinal(500);
    }
    
    /**
     * The given key gets used for a lookup via a properties file
     *
     * @param key for the property
     * @return value for the given key or null if there is no configured value
     */
    @Override
    public String getValue(String key) {
        if(super.isEnabled())return this.properties.get(key);
        return null;
    }

    @Override
    public String getName() {
        return CLASS_KEY_PREFIX + UNDERSCORE + this.fileUrl;
    }

    @Override
    public Map<String, String> getPropertiesIfEnabled() {
        return this.properties;
    }

    private Map<String, String> loadProperties(String surl) {
        Map<String,String> map = new HashMap<>();
        String urls[] = surl.split(COMMA);
        
        for(String url:urls){
            if(url!=null && !url.isEmpty()){
                map.putAll(loadProperty(url.trim()));
            }
        }
        return map;
    }
    
    private Map<String, String> loadProperty(String url) {
        log.log(Level.INFO, "Using [{0}] as properties URL", url);
        Properties props = new Properties();
        
        URL u;
        InputStream inputStream = null;
        
        try {
            u = new URL(url);
            inputStream = u.openStream();
            if (inputStream != null) {
                
                props.load(inputStream);
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Unable to read URL [{0}] - {1}", new Object[]{url, e.getMessage()});
        } finally {
            try {
                if (inputStream != null)inputStream.close();
            // no worries, means that the file is already closed
            } catch (IOException e) {}
        }
        return (Map) props;
    }
    
    private String getDefaultURL(String path){
        try {
            URL url = Paths.get(path).toUri().toURL();
            return url.toString();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private String loadPropertiesFileUrlPath(){
        Config cfg = ConfigProvider.getConfig();
        String url = cfg.getOptionalValue(KEY_URL, String.class).orElse(getDefaultURL(DEFAULT_URL));
        
        return url;
    }
    
    private static final String UNDERSCORE = "_";
    private static final String COMMA = ",";
}
