package org.microprofileext.config.source.memory.event;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * an event on memory config
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class MemoryConfigEvent implements Serializable {
    private Type type;
    private String key;
    private String oldValue;
    private String newValue;   
}
