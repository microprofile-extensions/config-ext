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

import java.util.NoSuchElementException;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import org.apache.geronimo.config.ConfigImpl;
import org.apache.geronimo.config.cdi.ConfigExtension;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:dpmoore@acm.org">Derek P. Moore</a>
 */
@RunWith(Arquillian.class)
public class DisabledWhenEnabledKeyIsFalseTest {

    @Inject
    Config config;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, ConfigImpl.class.getPackage())
                .addPackages(true, Config.class.getPackage())
                .addAsServiceProviderAndClasses(Extension.class, ConfigExtension.class)
                .addAsServiceProviderAndClasses(ConfigSource.class, FileConfigSource.class)
                .addAsResource(DisabledWhenEnabledKeyIsFalseTest.class.getClassLoader().getResource("file-config-disabled.properties"), "META-INF/microprofile-config.properties")
                .addAsManifestResource("META-INF/beans.xml");
    }

    @Test(expected = NoSuchElementException.class)
    public void testPropertyFailsWhenExplicitlyDisabled() {
        config.getValue("test.property", String.class);
    }

}
