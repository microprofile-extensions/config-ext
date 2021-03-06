/*
 * Copyright 2018 Derek P. Moore.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.microprofileext.config.source.base;

import java.util.Collections;
import java.util.Map;
import org.eclipse.microprofile.config.Config;

/**
 * A config source that can be disabled by class or by instance (all vs each).
 * <p>
 * Instance keys take precedence over class keys, so individual sources can be
 * turned back on if all sources have been turned off.
 * <p>
 * Config sources are enabled by default.
 *
 * @author <a href="mailto:dpmoore@acm.org">Derek P. Moore</a>
 */
public abstract class EnabledConfigSource extends BaseConfigSource {

    /**
     * Called to return the properties in this config source when it is enabled
     * @return the map containing the properties in this config source
     */
    abstract protected Map<String, String> getPropertiesIfEnabled();

    /**
     * Return the properties, unless disabled return empty
     * @return the map containing the properties in this config source or empty
     *         if disabled
     */
    @Override
    public Map<String, String> getProperties() {
        return isEnabled() ? getPropertiesIfEnabled() : Collections.emptyMap();
    }

    protected boolean isEnabled() {
        Config cnf = getConfig();
        return cnf.getOptionalValue(getInstanceEnableKey(), Boolean.class)
                .orElse(cnf.getOptionalValue(getClassEnableKey(), Boolean.class)
                        .orElse(true));
    }

    protected String getClassKeyPrefix(){
        return getClass().getSimpleName();
    }
    
    private String getClassEnableKey(){
        return getClassKeyPrefix() + ".enabled";
    }
    
    private String getInstanceEnableKey(){
        return getName() + ".enabled";
    }
}
