package org.microprofileext.config.event;

import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * an event on config element
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Data @AllArgsConstructor
public class ChangeEvent implements Serializable {
    
    private Type type;
    private String key;
    private Optional<String> oldValue;
    private String newValue;   
    private String fromSource;
    
}
