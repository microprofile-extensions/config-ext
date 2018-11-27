package org.microprofileext.config.source.base.file;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import lombok.extern.java.Log;

/**
 * Watching files for changes
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
public class FileWatcher {

    private WatchService watcher;
    
    private final Map<WatchKey,Path> directoryWatchers = new HashMap<>();
    private final Map<Path,List<String>> filterMap = new HashMap<>();
    private final long pollInterval;
    private final Reloadable reloadable;
    
    private ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
    
    public FileWatcher(Reloadable reloadable, long pollInterval){
        this.reloadable = reloadable;
        this.pollInterval = pollInterval;
        
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
    
    public void startWatching(URL url){
        try {
            Path path = Paths.get(url.toURI());
            Path dir = path.getParent();
            String filename = path.getFileName().toString();
            startWatching(dir,filename);
        } catch (URISyntaxException ex) {
            log.log(Level.WARNING, "Can not watch url [" + url +"]", ex);
        }
    }
    
    public void startWatching(Path path,String filter){
        if(filterMap.containsKey(path)){
            // Already watching this directory
            if(!filterMap.get(path).contains(filter))filterMap.get(path).add(filter);
        }else {
            // New folder to monitor
            List<String> filters = new ArrayList<>();
            filters.add(filter);
            filterMap.put(path, filters);
            
            try {
                WatchKey key = path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
                directoryWatchers.put(key, path);
                // Here start Runable
                scheduledThreadPool.schedule(new DirectoryPoller(),this.pollInterval,TimeUnit.SECONDS);
            } catch (IOException ex) {
                log.log(Level.WARNING, "Could not register directory [{0}] to watch for changes - {1}", new Object[]{path, ex.getMessage()});
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    
    class DirectoryPoller implements Runnable{
    
        @Override
        public void run() {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
             }

            Path d = directoryWatchers.get(key);
            if (d != null) {

                for (WatchEvent<?> event: key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW)continue;
                    
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    List<String> filters = filterMap.get(d);

                    if(filters.contains(name.toString())){
                        Path child = d.resolve(name);
                        try {
                            reloadable.reload(child.toUri().toURL());
                        } catch (MalformedURLException ex) {
                            log.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            
            boolean valid = key.reset();
            if (!valid)directoryWatchers.remove(key);
            
            this.run();
        }
    }
}