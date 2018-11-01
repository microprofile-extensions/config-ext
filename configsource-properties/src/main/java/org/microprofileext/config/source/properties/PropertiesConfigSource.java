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
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.microprofileext.config.source.base.AbstractUrlBasedSource;

/**
 * Properties config source
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 * @author <a href="mailto:dpmoore@acm.org">Derek P. Moore</a>
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
public class PropertiesConfigSource extends AbstractUrlBasedSource {

    @Override
    protected String getFileExtension() {
        return "properties";
    }

    @Override @SuppressWarnings("unchecked")
    protected Map<String, String> toMap(InputStream inputStream) {
        Properties props = new Properties();
        try {    
            props.load(inputStream);
            if(!keySeparator.equalsIgnoreCase(DOT))props = changeKeySeparator(props);
        } catch (IOException e) {
            log.log(Level.WARNING, "Unable to load properties [{0}]", e.getMessage());
        }
        return (Map) props;
    }
    
    private Properties changeKeySeparator(Properties props){
        Properties nprops = new Properties();
        Set<Object> keySet = props.keySet();
        for(Object k:keySet){
            String key = (String)k;
            String value = props.getProperty(key);
            key = key.replaceAll("\\" + DOT, keySeparator);
            nprops.put(key, value);
        }
        
        return nprops;
    }
    
    private static final String DOT = ".";
}
