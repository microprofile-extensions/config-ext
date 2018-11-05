package org.microprofileext.config.example;

import java.util.logging.Level;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import lombok.extern.java.Log;
import org.microprofileext.config.source.memory.event.EventKey;
import org.microprofileext.config.source.memory.event.EventType;
import org.microprofileext.config.source.memory.event.MemoryConfigEvent;
import org.microprofileext.config.source.memory.event.Type;

/**
 * Example Service. JAX-RS
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
@RequestScoped
public class MemoryEventListener {

    // Getting all memory config event
    public void all(@Observes MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "ALL: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Get only new values
    public void newValue(@Observes @EventType(Type.NEW) MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "NEW: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Get only override values
    public void overrideValue(@Observes @EventType(Type.OVERRIDE) MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "OVERRIDE: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Get only revert values
    public void revertValue(@Observes @EventType(Type.REVERT) MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "REVERT: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Getting all memory config event when key is some.key
    public void allForKey(@Observes @EventKey("some.key") MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "ALL for key [some.key]: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Getting all memory config event when key is some.key for new events
    public void newForKey(@Observes @EventType(Type.NEW) @EventKey("some.key") MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "NEW for key [some.key]: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Getting all memory config event when key is some.key for override events
    public void overrideForKey(@Observes @EventType(Type.OVERRIDE) @EventKey("some.key") MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "OVERRIDE for key [some.key]: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Getting all memory config event when key is some.key for revert events
    public void revertForKey(@Observes @EventType(Type.REVERT) @EventKey("some.key") MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "REVERT for key [some.key]: Received a memory config event: {0}", memoryConfigEvent);
    }
}
