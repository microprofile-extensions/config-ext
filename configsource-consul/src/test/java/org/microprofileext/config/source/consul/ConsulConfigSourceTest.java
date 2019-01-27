package org.microprofileext.config.source.consul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;

class ConsulConfigSourceTest {

    private ConsulConfigSource configSource;

    @BeforeEach
    public void init() {
        configSource = new ConsulConfigSource(mock(ConsulClient.class));
    }
    
    @Test
    void testGetProperties_empty() {
        assertTrue(configSource.getProperties().isEmpty());
    }

    @Test
    void testGetProperties_one() {
        GetValue value = new GetValue();
        value.setValue(Base64.getEncoder().encodeToString("hello".getBytes()));
        when(configSource.client.getKVValue(anyString())).thenReturn(new Response<GetValue>(value, 0L, true, 0L));
        configSource.getValue("test");
        assertEquals(1, configSource.getProperties().size());
    }

    @Test
    void testGetValue_null() {
        when(configSource.client.getKVValue(anyString())).thenReturn(new Response<GetValue>(null, 0L, true, 0L));
        assertNull(configSource.getValue("test"));
    }

    @Test
    void testGetValue() {
        GetValue value = new GetValue();
        value.setValue(Base64.getEncoder().encodeToString("hello".getBytes()));
        when(configSource.client.getKVValue(anyString())).thenReturn(new Response<GetValue>(value, 0L, true, 0L));
        assertEquals("hello", configSource.getValue("test"));
    }

    @Test
    void testGetValue_cache() {
        GetValue value = new GetValue();
        value.setValue(Base64.getEncoder().encodeToString("hello".getBytes()));
        when(configSource.client.getKVValue(anyString())).thenReturn(new Response<GetValue>(value, 0L, true, 0L));
        configSource.getValue("test");
        configSource.getValue("test");
        verify(configSource.client, times(1)).getKVValue("test");
    }

    @Test
    void testGetValue_exception() {
        when(configSource.client.getKVValue(anyString())).thenThrow(RuntimeException.class);
        assertNull(configSource.getValue("test"));
    }

    @Test
    void testGetValue_prefix() {
        System.setProperty("configsource.consul.prefix", "myprefix");
        // reinitialize after system property set (systemproperty have been cached in default configsource)
        configSource = new ConsulConfigSource(mock(ConsulClient.class));
        GetValue value = new GetValue();
        value.setValue(Base64.getEncoder().encodeToString("hello".getBytes()));
        when(configSource.client.getKVValue(anyString())).thenReturn(new Response<GetValue>(value, 0L, true, 0L));
        configSource.getValue("test");
        System.clearProperty("configsource.consul.prefix");
        verify(configSource.client, times(1)).getKVValue("myprefix/test");
    }

}
