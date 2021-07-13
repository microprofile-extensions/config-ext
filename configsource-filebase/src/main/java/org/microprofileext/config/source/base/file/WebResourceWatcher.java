package org.microprofileext.config.source.base.file;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Watching web resources for changes
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
public class WebResourceWatcher {

    private static final Logger log = Logger.getLogger(WebResourceWatcher.class.getName());
    
    private final long pollInterval;
    private final Reloadable reloadable;
    
    private final ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
    
    private final Map<URL,Long> urlsToWatch = new HashMap<>();
    
    public WebResourceWatcher(Reloadable reloadable, long pollInterval){
        this.reloadable = reloadable;
        this.pollInterval = pollInterval;
        scheduledThreadPool.scheduleAtFixedRate(new Poller(),this.pollInterval,this.pollInterval,TimeUnit.SECONDS);
    }
    
    public void startWatching(URL url){
        if(!urlsToWatch.containsKey(url)){
            long lastModified = getLastModified(url);
            if(lastModified>0){
                urlsToWatch.put(url,lastModified);
            }else{
                log.log(Level.WARNING, "Can not poll {0} for changes, lastModified not implemented", url);
            }
        }
    }
    
    private long getLastModified(URL url){
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(HEAD);
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            return con.getLastModified();
        } catch (IOException ex) {
            log.log(Level.SEVERE, ex.getMessage());
        } finally {
            if(con!=null)con.disconnect();
        }
        
        return -1;
    }
          
    private static final String HEAD = "HEAD";    
    
    class Poller implements Runnable{
    
        @Override
        public void run() {
            Set<URL> urls = urlsToWatch.keySet();
            for(URL url : urls){
                long lastModified = getLastModified(url);
                if(lastModified!=urlsToWatch.get(url)){
                    urlsToWatch.put(url, lastModified);
                    reloadable.reload(url);
                }
            }   
        }
    }
}