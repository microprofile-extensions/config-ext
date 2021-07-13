package org.microprofileext.config.source.yaml;

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
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
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
                .addAsServiceProviderAndClasses(ConfigSource.class, YamlConfigSource.class)
                .addAsResource(DisabledWhenEnabledKeyIsFalseTest.class.getClassLoader().getResource("config-disabled.properties"), "META-INF/microprofile-config.properties")
                .addAsManifestResource("META-INF/beans.xml");
    }

    @Test(expected = NoSuchElementException.class)
    public void testPropertyFailsWhenExplicitlyDisabled() {
        config.getValue("test.property", String.class);
    }

}
