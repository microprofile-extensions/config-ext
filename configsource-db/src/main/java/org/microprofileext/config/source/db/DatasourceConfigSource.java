package org.microprofileext.config.source.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.eclipse.microprofile.config.spi.ConfigSource;

import lombok.extern.java.Log;

@Log
public class DatasourceConfigSource implements ConfigSource {

    Configuration config = new Configuration();
    private Map<String, TimedEntry> cache = new ConcurrentHashMap<>();
    Repository repository = null;

    @Override
    public Map<String, String> getProperties() {
        if (repository == null) {
            // late initialization is needed because of the EE datasource.
            repository = new Repository(config);
        }
        return repository.getAllConfigValues();
    }

    @Override
    public String getValue(String propertyName) {
        if (repository == null) {
            // late initialization is needed because of the EE datasource.
            repository = new Repository(config);
        }
        TimedEntry entry = cache.get(propertyName);
        if (entry == null || entry.isExpired()) {
            log.log(Level.FINE, () -> "load " + propertyName + " from db");
            String value = repository.getConfigValue(propertyName);
            cache.put(propertyName, new TimedEntry(value));
            return value;
        }
        return entry.getValue();
    }

    @Override
    public String getName() {
        return "DatasourceConfigSource";
    }

    @Override
    public int getOrdinal() {
        return 120;
    }
    
    class TimedEntry {
        private final String value;
        private final long timestamp;

        public TimedEntry(String value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        public String getValue() {
            return value;
        }

        public boolean isExpired() {
            return (timestamp + config.getValidity()) < System.currentTimeMillis();
        }
    }
}
