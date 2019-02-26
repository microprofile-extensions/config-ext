package org.microprofileext.config.source.typesafeconfig;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TypeSafeConfigConfigSourceTest {

    private TypeSafeConfigConfigSource configSource;
    private Config testConfig;

    @BeforeEach
    public void init() {
        InputStream asStream = TypeSafeConfigConfigSourceTest.class.getClassLoader().getResourceAsStream("TestConfig.conf");
//        testConfig = ConfigFactory.parseResources(TypeSafeConfigConfigSourceTest.class, "TestConfig.conf");
        testConfig = ConfigFactory.parseResources(
                TypeSafeConfigConfigSourceTest.class.getClassLoader(),
                "TestConfig.conf");
        configSource = new TypeSafeConfigConfigSource(testConfig);
    }
    
    @Test
    void testGetProperties_empty() {
        TypeSafeConfigConfigSource emptyConfigSource = new TypeSafeConfigConfigSource(mock(Config.class));
        assertTrue(emptyConfigSource.getProperties().isEmpty());
    }

    @Test
    void testGetProperties_one() {
        // As properties are added to TestConfig.conf this will need to be incremented to match
        val NUM_EXPECTED_PROPERTIES = 2;
        assertEquals(NUM_EXPECTED_PROPERTIES, configSource.getProperties().size());
    }

    @Test
    void testGetValue_null() {
        assertNull(configSource.getValue("thisKeyDoestNotExist"));
    }

    @Test
    void testGetValue() {
        assertEquals("hello", configSource.getValue("test"));
    }


    @Test
    void testGetValue_exception() {
        // Retrieving a value that is not stringable from the configuration will result in an exception from TypeSafe
        // config which should return null through this interface.
        assertNull(configSource.getValue("config_object"));
    }

}
