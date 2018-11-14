package org.microprofileext.config.event;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.faulttolerance.Asynchronous;

/**
 * Easy way to fire a change event
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 * 
 * This gets used from Config sources that is not in the CDI Context. So we can not @Inject a bean.
 * For some reason, CDI.current() is only working on Payara, and not on Thorntail and OpenLiberty, so this ugly footwork is to
 * get around that.
 */
@Log
@ApplicationScoped
public class ChangeEventNotifier {
    
    private static ChangeEventNotifier INSTANCE;
    
    @Inject
    private Event<ChangeEvent> broadcaster;
    
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        INSTANCE = this;
    }
 
    public static ChangeEventNotifier getInstance(){
        return INSTANCE;
    }
    
    @Asynchronous
    public Future<Void> detectChangesAndFire(Map<String, String> before,Map<String, String> after, String fromSource){
        List<ChangeEvent> changes = new ArrayList<>();
        if(!before.equals(after)){
            Set<Map.Entry<String, String>> beforeEntries = before.entrySet();
            for(Map.Entry<String,String> beforeEntry:beforeEntries){
                String key = beforeEntry.getKey();
                String oldValue = beforeEntry.getValue();
                if(after.containsKey(key)){
                    String newValue = after.get(key);
                    if(!newValue.equals(oldValue)){
                        // Update
                        changes.add(new ChangeEvent(Type.UPDATE, key, getOptionalOldValue(oldValue), newValue, fromSource));
                    }
                    after.remove(key);
                }else{
                    // Removed.
                    changes.add(new ChangeEvent(Type.REMOVE, key, getOptionalOldValue(oldValue), null, fromSource));
                }
            }
            
            Set<Map.Entry<String, String>> newEntries = after.entrySet();
            for(Map.Entry<String,String> newEntry:newEntries){
                // New
                changes.add(new ChangeEvent(Type.NEW, newEntry.getKey(), Optional.empty(), newEntry.getValue(), fromSource));
            }
        }
        if(!changes.isEmpty())fire(changes);
        return CompletableFuture.completedFuture(null);
    }
    
    @Asynchronous
    public Future<Void> fire(ChangeEvent changeEvent){
        List<Annotation> annotationList = new ArrayList<>();
        annotationList.add(new TypeFilter.TypeFilterLiteral(changeEvent.getType()));
        annotationList.add(new KeyFilter.KeyFilterLiteral(changeEvent.getKey()));
        annotationList.add(new SourceFilter.SourceFilterLiteral(changeEvent.getFromSource()));
        
        broadcaster.select(annotationList.toArray(new Annotation[annotationList.size()])).fire(changeEvent);
        return CompletableFuture.completedFuture(null);
    }
    
    @Asynchronous
    public Future<Void> fire(List<ChangeEvent> changeEvents){
        for(ChangeEvent changeEvent : changeEvents){
            fire(changeEvent);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    private Optional<String> getOptionalOldValue(String oldValue){
        if(oldValue==null || oldValue.isEmpty())return Optional.empty();
        return Optional.of(oldValue);
    }
    
}
