package org.microprofileext.config.source.consul;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

public class Configuration {

    private Config config = ConfigProviderResolver.instance()
            .getBuilder()
            .addDefaultSources()
            .build();

    private String consulHost = getConfigValue("configsource.consul.host", "");
    private String consulHostList = getConfigValue("configsource.consul.hosts", "");
    private int consulPort = Integer.valueOf(getConfigValue("configsource.consul.port", "8500"));
    private long validity = Long.valueOf(getConfigValue("configsource.consul.validity", "30")) * 1000L;
    private String prefix = addSlash(getConfigValue("configsource.consul.prefix", ""));
    private String token = getConfigValue("configsource.consul.token", null);
    private boolean listAll = Boolean.valueOf(getConfigValue("configsource.consul.list-all", "false"));

    public long getValidity() {
        return validity;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getToken() {
        return token;
    }

    public String getConsulHost() {
        return consulHost;
    }

    public String getConsulHostList() {
        return consulHostList;
    }

    public int getConsulPort() {
        return consulPort;
    }

    public boolean listAll() {
        return listAll;
    }

    private String getConfigValue(String key, String defaultValue) {
        return config.getOptionalValue(key, String.class).orElse(defaultValue);
    }

    private String addSlash(String envOrSystemProperty) {
        return envOrSystemProperty.isEmpty() ? "" : envOrSystemProperty + "/";
    }

}
