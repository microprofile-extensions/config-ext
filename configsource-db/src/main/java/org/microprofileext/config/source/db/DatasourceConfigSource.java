package org.microprofileext.config.source.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.microprofileext.config.source.base.EnabledConfigSource;

import lombok.extern.java.Log;

@Log
public class DatasourceConfigSource extends EnabledConfigSource {

    private Map<String, TimedEntry> cache = new ConcurrentHashMap<>();
    Repository repository = null;
    private Long validity = null;
    
    public DatasourceConfigSource() {
        log.info("Loading [db] MicroProfile ConfigSource");
        super.initOrdinal(120);
    }

    @Override
    public Map<String, String> getPropertiesIfEnabled() {
        if (repository == null) {
            // late initialization is needed because of the EE datasource.
            repository = new Repository(getConfig());
        }
        return repository.getAllConfigValues();
    }

    @Override
    public String getValue(String propertyName) {
        if (CONFIG_ORDINAL.equals(propertyName)) {
            return null;
        }
        if (repository == null) {
            // late initialization is needed because of the EE datasource.
            repository = new Repository(getConfig());
        }
        if (validity == null) {
            validity = getConfig().getOptionalValue("configsource.db.validity", Long.class).orElse(30000L);
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

//    @Override
//    public int getOrdinal() {
//        return 120;
//    }
    
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
            return (timestamp + validity) < System.currentTimeMillis();
        }
    }
}
