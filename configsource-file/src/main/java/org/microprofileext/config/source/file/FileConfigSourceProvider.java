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
package org.microprofileext.config.source.file;

import java.util.Collections;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

/**
 * @author <a href="mailto:dpmoore@acm.org">Derek P. Moore</a>
 */
public class FileConfigSourceProvider implements ConfigSourceProvider { //extends EnabledConfigSourceProvider<FileConfigSource> {

    private static final String KEY_FILE_URI = "FileConfigSource.fileuri";
    private static final String DEFAULT_FILE_URI = "application.properties";

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        Config cfg = ConfigProviderResolver.instance()
                .getBuilder()
                .addDefaultSources()
                .build();

        String file = cfg.getOptionalValue(KEY_FILE_URI, String.class).orElse(DEFAULT_FILE_URI);

        try {
            return Collections.singletonList(new FileConfigSource(file));
        } catch (Exception e) {
            
        }

        return Collections.emptyList();
    }

}
