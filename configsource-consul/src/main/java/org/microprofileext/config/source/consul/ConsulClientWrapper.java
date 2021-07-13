package org.microprofileext.config.source.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConsulClientWrapper {
    private static final Logger log = Logger.getLogger(ConsulClientWrapper.class.getName());

    private String host;
    private List<String> peers = null;
    private int port;
    private String token;
    ConsulClient client = null;

    public ConsulClientWrapper(String host, String hosts, int port, String token) {
        this.host = host;
        if (hosts != null && !hosts.isEmpty()) {
            peers = Arrays.asList(hosts.split(","));
        }
        this.port = port;
        this.token = token;
    }

    public ConsulClient getClient() {
        initConsulClient();
        return client;
    }

    public String getValue(String key) {
        try {
            String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8.name());
            GetValue value = retry(2, () -> getClient().getKVValue(encodedKey, token).getValue(), () -> forceReconnect());
            return value == null ? null : value.getDecodedValue();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            forceReconnect();
            throw e;
        }
    }

    public List<Entry<String, String>> getKeyValuePairs(String prefix) {
        try {
            String encodedPrefix = URLEncoder.encode(prefix, StandardCharsets.UTF_8.name());
            List<GetValue> values = retry(2, () -> getClient().getKVValues(encodedPrefix, token).getValue(), () -> forceReconnect());
            return values.stream()
                    .map(v -> new SimpleEntry<String, String>(v.getKey(), v.getDecodedValue()))
                    .collect(Collectors.toList());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            forceReconnect();
            throw e;
        }
    }

    void initConsulClient() {
        if (peers == null) {
            client = new ConsulClient(host, port);
            peers = client.getStatusPeers().getValue().stream()
                    .map(s -> s.split(":")[0])
                    .collect(Collectors.toList());
        }
        if (client == null) {
            client = getClientToAnyConsulHost();
        }
    }

    private ConsulClient getClientToAnyConsulHost() {
        return peers.stream()
                .map(host -> new ConsulClient(host, port))
                .filter(this::isConsulReachable)
                .findAny()
                .orElseThrow(() -> new RuntimeException("No Consul host could be reached."));
    }

    private boolean isConsulReachable(ConsulClient client) {
        try {
            Response<String> leader = client.getStatusLeader();
            log.log(Level.INFO, () -> "Successfully established connection to Consul. Current cluster leader is " + leader.getValue());
        } catch (Exception e) {
            log.log(Level.INFO, () -> "Could not establish connection to consul: " + e.getMessage());
            return false;
        }
        return true;
    }

    private <T> T retry(int maxRetries, Supplier<T> supplier, Runnable onFailedAttempt) {
        int retries = 0;
        RuntimeException lastException = null;
        while (retries <= maxRetries) {
            try {
                return supplier.get();
            } catch (RuntimeException e) {
                lastException = e;
                onFailedAttempt.run();
                retries++;
            }
        }
        throw lastException;
    }

    private void forceReconnect() {
        client = null;
    }

}
