package org.microprofileext.config.source.db;

import org.eclipse.microprofile.config.Config;
import org.microprofileext.config.source.base.EnabledConfigSource;
import org.microprofileext.config.source.base.ExpiringMap;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DatasourceConfigSource extends EnabledConfigSource {

    private static final Logger log = Logger.getLogger(DatasourceConfigSource.class.getName());
    
    private static final String VALIDITY_KEY = "configsource.db.validity";
    private static final String CONFIGSOURCE_ORDINAL_KEY = "configsource.db.ordinal";

    private Config config;
    Repository repository = null;
    ExpiringMap<String, String> cache = null;

    public DatasourceConfigSource() {
        config = getConfig();
        log.info("Loading [db] MicroProfile ConfigSource");
        super.initOrdinal(450);
    }

    @Override
    protected Map<String, String> getPropertiesIfEnabled() {
        initRepository();
        try {
            return repository.getAllConfigValues();
        } catch (SQLException e) {
            log.info("query failed: " + e.getMessage());
            clearRepository();
        }
        return new HashMap<>();
    }

    @Override
    public String getValue(String propertyName) {
        initRepository();
        initCache();

        return cache.getOrCompute(propertyName, p -> repository.getConfigValue(p), (e) -> clearRepository());
    }

    @Override
    public String getName() {
        return "DatasourceConfigSource";
    }

    @Override
    public int getOrdinal() {
        return config.getOptionalValue(CONFIGSOURCE_ORDINAL_KEY, Integer.class).orElse(450);
    }

    private void initRepository() {
        if (repository == null) {
            // late initialization is needed because of the EE datasource.
            repository = new Repository(config);
        }
    }

    void clearRepository() {
        repository = null;
    }

    private void initCache() {
        if (cache == null) {
            long validity = config.getOptionalValue(VALIDITY_KEY, Long.class).orElse(30000L);
            cache = new ExpiringMap<>(validity);
        }
    }

}
