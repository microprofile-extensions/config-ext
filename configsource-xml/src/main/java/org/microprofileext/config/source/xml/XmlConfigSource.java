package org.microprofileext.config.source.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.microprofileext.config.source.base.AbstractUrlBasedSource;

/**
 * Xml config source
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Log
public class XmlConfigSource extends AbstractUrlBasedSource {
    
    @Override
    protected String getFileExtension() {
        return "xml";
    }
    
    @Override
    protected Map<String, String> loadUrl(String url) {
        log.log(Level.INFO, "Using [{0}] as xml URL", url);
        Map<String,String> map = new HashMap<>();
        
        URL u;
        InputStream inputStream = null;
        
        try {
            u = new URL(url);
            inputStream = u.openStream();
            if (inputStream != null) {
                XmlConverter yamlConverter = new XmlConverter(inputStream);
                map = yamlConverter.getProperties();
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Unable to read URL [{0}] - {1}", new Object[]{url, e.getMessage()});
        } finally {
            try {
                if (inputStream != null)inputStream.close();
            // no worries, means that the file is already closed
            } catch (IOException e) {}
        }
        return map;
    }
}