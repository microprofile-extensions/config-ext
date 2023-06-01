package org.microprofileext.config.source.json;

import java.util.NoSuchElementException;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;
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
                .addPackages(true, Config.class.getPackage())
                .addAsServiceProviderAndClasses(ConfigSource.class, JsonConfigSource.class)
                .addAsResource(DisabledWhenEnabledKeyIsFalseTest.class.getClassLoader().getResource("config-disabled.properties"), "META-INF/microprofile-config.properties")
                .addAsManifestResource("META-INF/beans.xml");
    }

    @Test(expected = NoSuchElementException.class)
    public void testPropertyFailsWhenExplicitlyDisabled() {
        config.getValue("test.property", String.class);
    }

}
