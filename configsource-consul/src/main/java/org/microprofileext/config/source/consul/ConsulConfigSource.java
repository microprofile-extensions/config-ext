package org.microprofileext.config.source.consul;

import lombok.extern.java.Log;
import org.microprofileext.config.source.base.EnabledConfigSource;
import org.microprofileext.config.source.base.ExpiringMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class ConsulConfigSource extends EnabledConfigSource {

    private static final String DEFAULT_CONSUL_CONFIGSOURCE_ORDINAL = "550";

    Configuration config = new Configuration();
    ExpiringMap<String, String> cache = new ExpiringMap<>(config.getValidity());
    boolean isDisabled = config.getConsulHost().isEmpty() && config.getConsulHostList().isEmpty();
    ConsulClientWrapper client = new ConsulClientWrapper(config.getConsulHost(), config.getConsulHostList(), config.getConsulPort(), config.getToken());

    public ConsulConfigSource() {
        log.info("Loading [consul] MicroProfile ConfigSource");
        super.initOrdinal(550);
    }

    @Override
    public Map<String, String> getPropertiesIfEnabled() {
        // only query for values if explicitly enabled
        if (!isDisabled && config.listAll()) {
            List<Entry<String, String>> values = client.getKeyValuePairs(config.getPrefix());
            values.forEach(v -> cache.put(v.getKey(), v.getValue()));
        }
        return cache.getMap().entrySet()
                .stream()
                .filter(e -> e.getValue().get() != null)
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().get()));
    }

    @Override
    public String getValue(String propertyName) {
        if (isDisabled) {
            return null;
        }
        String value = cache.getOrCompute(propertyName,
                p -> client.getValue(config.getPrefix() + propertyName),
                p -> log.log(Level.FINE, () -> "consul getKV failed for key " + p));
        // use default if config_ordinal not found
        if (CONFIG_ORDINAL.equals(propertyName)) {
            return Optional.ofNullable(value).orElse(DEFAULT_CONSUL_CONFIGSOURCE_ORDINAL);
        }
        return value;
    }

    @Override
    public String getName() {
        return "ConsulConfigSource";
    }

    @Override
    public int getOrdinal() {
        return getConfig().getOptionalValue(CONFIG_ORDINAL, Integer.class).orElse(550);
    }
}
