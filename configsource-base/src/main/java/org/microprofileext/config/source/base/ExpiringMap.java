package org.microprofileext.config.source.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * A simple map with an expiring policy. Note: this map can also store null
 * values!
 *
 * @param <K> key
 * @param <V> value
 */
public class ExpiringMap<K, V> {
    long validity;
    private Map<K, TimedEntry<V>> cache = new ConcurrentHashMap<>();

    public ExpiringMap(long validity) {
        this.validity = validity;
    }

    /**
     * Similar to Map::computeIfAbsent, but the mapping function can throw an
     * exception, which can be handled with a consumer.
     * 
     * @param key         key
     * @param action      mapping function
     * @param onException exception handler
     * @return
     */
    public V getOrCompute(K key, CheckedFunction<K, V> action, Consumer<K> onException) {
        TimedEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            try {
                V value = action.apply(key);
                put(key, value);
                return value;
            } catch (Exception e) {
                onException.accept(key);
            }
        }
        // if the entry was never cached, then it will be null
        return entry != null ? entry.get() : null;
    }

    public void put(K key, V value) {
        cache.put(key, new TimedEntry<V>(value));
    }

    /**
     * Access underlying map, use with care
     * 
     * @return
     */
    public Map<K, TimedEntry<V>> getMap() {
        return cache;
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }

    public class TimedEntry<E> {
        private final E value;
        private final long timestamp;

        public TimedEntry(E value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        public E get() {
            return value;
        }

        public boolean isExpired() {
            return (timestamp + validity) < System.currentTimeMillis();
        }
    }

}
