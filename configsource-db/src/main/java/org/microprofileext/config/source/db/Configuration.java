package org.microprofileext.config.source.db;

import java.util.Optional;

public class Configuration {
    private long validity = Long.valueOf(getEnvOrSystemProperty("mp-config-db.validity", "30")) * 1000L;
    private String datasourceJndi = getEnvOrSystemProperty("mp-config-db.datasource", "java:comp/DefaultDataSource");
    private String table = getEnvOrSystemProperty("mp-config-db.table", "configuration");
    private String keyColumn = getEnvOrSystemProperty("mp-config-db.keyColumn", "key");
    private String valueColumn = getEnvOrSystemProperty("mp-config-db.valueColumn", "value");

    public String getDatasourceJndi() {
        return datasourceJndi;
    }

    public long getValidity() {
        return validity;
    }

    public String getTable() {
        return table;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public String getValueColumn() {
        return valueColumn;
    }

    private static String getEnvOrSystemProperty(String key, String defaultValue) {
        return Optional.ofNullable(System.getenv(key)).orElse(System.getProperty(key, defaultValue));
    }

}
