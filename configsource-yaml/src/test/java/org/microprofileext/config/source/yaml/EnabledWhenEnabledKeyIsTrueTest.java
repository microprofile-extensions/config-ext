package org.microprofileext.config.source.yaml;

import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;
import static org.assertj.core.api.Assertions.assertThat;
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
public class EnabledWhenEnabledKeyIsTrueTest {

    @Inject
    Config config;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, Config.class.getPackage())
                .addAsServiceProviderAndClasses(ConfigSource.class, YamlConfigSource.class)
                .addAsResource(EnabledWhenEnabledKeyIsTrueTest.class.getClassLoader().getResource("config-enabled.properties"), "META-INF/microprofile-config.properties")
                .addAsManifestResource("META-INF/beans.xml");
    }

    @Test
    public void testPropertyLoadsWhenExplicitlyEnabled() {
        assertThat(config.getOptionalValue("test.property", String.class)).get()
                .isEqualTo("a-string-value")
                .as("test.property in application.properties is set to a-string-value");
    }

}
