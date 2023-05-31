package org.microprofileext.config.example;

import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.EventMetadata;
import org.microprofileext.config.event.ChangeEvent;
import org.microprofileext.config.event.Type;
import org.microprofileext.config.event.KeyFilter;
import org.microprofileext.config.event.regex.RegexFilter;
import org.microprofileext.config.event.SourceFilter;
import org.microprofileext.config.event.TypeFilter;
import org.microprofileext.config.event.regex.Field;

/**
 * Example Listener for changes in the events
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@ApplicationScoped
public class EventChangeListener {

    private static final Logger log = Logger.getLogger(EventChangeListener.class.getName());
    
    // Getting all config event
    public void all(@Observes ChangeEvent changeEvent){
        log.log(Level.SEVERE, "ALL: Received a config change event: {0}", changeEvent);
    }
    
    // Get only new values
    public void newValue(@Observes @TypeFilter(Type.NEW) ChangeEvent changeEvent){
        log.log(Level.SEVERE, "NEW: Received a config change event: {0}", changeEvent);
    }
    
    // Get only override values
    public void overrideValue(@Observes @TypeFilter(Type.UPDATE) ChangeEvent changeEvent){
        log.log(Level.SEVERE, "UPDATE: Received a config change event: {0}", changeEvent);
    }
    
    // Get only revert values
    public void revertValue(@Observes @TypeFilter(Type.REMOVE) ChangeEvent changeEvent){
        log.log(Level.SEVERE, "REMOVE: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config event when key is some.key
    public void allForKey(@Observes @KeyFilter("some.key") ChangeEvent changeEvent){
        log.log(Level.SEVERE, "ALL for key [some.key]: Received a config change event: {0}", changeEvent);
    }
    
    @RegexFilter("^some\\..+") // Starting with some.
    public void allForPatternMatchOnKey(@Observes ChangeEvent changeEvent, EventMetadata meta){
        log.log(Level.SEVERE, "Pattern match on key: Received a config change event: {0}", changeEvent);
    }
    
    @RegexFilter(onField = Field.oldValue, value = "^some\\..+") // Starting with some.
    public void allForPatternMatchOnOldValue(@Observes ChangeEvent changeEvent, EventMetadata meta){
        log.log(Level.SEVERE, "Pattern match on old value: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config event when key is some.key for new events
    public void newForKey(@Observes @TypeFilter(Type.NEW) @KeyFilter("some.key") ChangeEvent changeEvent){
        log.log(Level.SEVERE, "NEW for key [some.key]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config event when key is some.key for override events
    public void overrideForKey(@Observes @TypeFilter(Type.UPDATE) @KeyFilter("some.key") ChangeEvent changeEvent){
        log.log(Level.SEVERE, "UPDATE for key [some.key]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config event when key is some.key for revert events
    public void revertForKey(@Observes @TypeFilter(Type.REMOVE) @KeyFilter("some.key") ChangeEvent changeEvent){
        log.log(Level.SEVERE, "REMOVE for key [some.key]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config events for a certain source
    public void allForSource(@Observes @SourceFilter("MemoryConfigSource") ChangeEvent changeEvent){
        log.log(Level.SEVERE, "ALL for source [MemoryConfigSource]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config events for a certain source
    public void allForSourceAndKey(@Observes @SourceFilter("MemoryConfigSource") @KeyFilter("some.key")  ChangeEvent changeEvent){
        log.log(Level.SEVERE, "ALL for source [MemoryConfigSource] and for key [some.key]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config events for a certain source
    public void overrideForSourceAndKey(@Observes @TypeFilter(Type.UPDATE) @SourceFilter("MemoryConfigSource") @KeyFilter("some.key")  ChangeEvent changeEvent){
        log.log(Level.SEVERE, "UPDATE for source [MemoryConfigSource] and for key [some.key]: Received a config change event: {0}", changeEvent);
    }
}
