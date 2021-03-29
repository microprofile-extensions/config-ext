package org.microprofileext.config.source.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.microprofileext.config.source.base.ExpiringMap;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DatasourceConfigSourceTest {

    private DatasourceConfigSource configSource;

    @BeforeEach
    public void init() {
        configSource = Mockito.spy(new DatasourceConfigSource());
        configSource.repository = mock(Repository.class);
        configSource.cache = new ExpiringMap<String, String>(30000);
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
    public void testGetProperties_null() throws Exception {
        when(configSource.repository.getConfigValue(anyString())).thenReturn(null);
        configSource.getValue("test");
        assertTrue(configSource.getProperties().isEmpty());
    }

    @Test
    public void testGetProperties_one() throws Exception {
        when(configSource.repository.getAllConfigValues()).thenReturn(Collections.singletonMap("test", "value"));
        configSource.getValue("test");
        assertEquals(1, configSource.getProperties().size());
    }

    @Test
    public void testGetValue() throws Exception {
        when(configSource.repository.getConfigValue(anyString())).thenReturn("123");
        assertEquals("123", configSource.getValue("test"));
    }

    @Test
    public void testGetValue_cache() throws Exception {
        when(configSource.repository.getConfigValue(anyString())).thenReturn("123");
        configSource.getValue("test");
        configSource.getValue("test");
        verify(configSource.repository, times(1)).getConfigValue(anyString());
    }

    @Test
    public void testGetValue_exception() throws Exception {
        when(configSource.repository.getConfigValue(anyString())).thenThrow(SQLException.class);
        assertNull(configSource.getValue("test"));
        verify(configSource, times(1)).clearRepository();
    }

    @Test
    public void testGetValue_exception_cache() throws Exception {

        // negative value, otherwise two serial "getValue" will have the same timestamp
        // and the entry is not yet expired.
        configSource.cache = new ExpiringMap<String, String>(-5);

        when(configSource.repository.getConfigValue(anyString())).thenReturn("123");
        assertEquals("123", configSource.getValue("test"));
        when(configSource.repository.getConfigValue(anyString())).thenThrow(SQLException.class);
        assertEquals("123", configSource.getValue("test")); // load from cache, also when the entry is already expired.
        verify(configSource, times(1)).clearRepository();

    }

}
