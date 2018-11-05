package org.microprofileext.config.example;

import java.util.logging.Level;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import lombok.extern.java.Log;
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
    
    // Getting all memory config event
    public void override(@Observes MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "ALL: Received a memory config event: {0}", memoryConfigEvent);
    }
}
