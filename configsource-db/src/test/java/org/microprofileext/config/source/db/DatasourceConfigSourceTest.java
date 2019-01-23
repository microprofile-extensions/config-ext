package org.microprofileext.config.source.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.microprofileext.config.source.db.DatasourceConfigSource;
import org.microprofileext.config.source.db.Repository;

class DatasourceConfigSourceTest {

    private DatasourceConfigSource configSource;

    @BeforeEach
    public void init() {
        configSource = new DatasourceConfigSource();
        configSource.repository = mock(Repository.class);
    }

    @Test
    void testGetOrdinal() {
        assertTrue(configSource.getOrdinal() > 100);
    }

    @Test
    void testGetProperties_empty() {
        assertTrue(configSource.getProperties().isEmpty());
    }

    @Test
    public void testGetProperties_null() {
        when(configSource.repository.getConfigValue(anyString())).thenReturn(null);
        configSource.getValue("test");
        assertTrue(configSource.getProperties().isEmpty());
    }

    @Test
    public void testGetProperties_one() {
        when(configSource.repository.getAllConfigValues()).thenReturn(Collections.singletonMap("test", "value"));
        configSource.getValue("test");
        assertEquals(1, configSource.getProperties().size());
    }

    @Test
    public void testGetValue() {
        when(configSource.repository.getConfigValue(anyString())).thenReturn("123");
        assertEquals("123", configSource.getValue("test"));
    }
    
    @Test
    public void testGetValue_cache() {
        when(configSource.repository.getConfigValue(anyString())).thenReturn("123");
        configSource.getValue("test");
        configSource.getValue("test");
        verify(configSource.repository, times(1)).getConfigValue(anyString());
    }

}
