package org.microprofileext.config.source.file;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import org.microprofileext.config.source.properties.url.PropertyFileConfigSource;

/**
 * File config source
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 * @author <a href="mailto:dpmoore@acm.org">Derek P. Moore</a>
 */
public class FileConfigSource extends PropertyFileConfigSource {

    /**
     * Initialize the {@link PropertyFileConfigSource} super class with a
     * {@code URL} and set its priority ordinal to {@code 199}.
     *
     * @param propertyFileUrl URL to a {@code .properties} file
     */
    public FileConfigSource(URL propertyFileUrl) {
        super(propertyFileUrl);
        initOrdinal(350);
    }

    /**
     * Get a {@code URL} from a path to a file on the default file system and
     * call {@link FileConfigSource(URL propertyFileUrl)}.
     *
     * @param externalFile path to a {@code .properties} file on the default
     *                     file system
     */
    public FileConfigSource(String externalFile) {
        this(loadExternalConfig(externalFile));
    }

    /**
     * Make a {@code URL} from a path to a file on the default file system and
     * ensure the file can be opened for reading.
     *
     * @param externalConfig path to a file on the default file system
     * @return a readable URL to a file on the default file system
     */
//    @Logged(level = LogLevel.INFO)
    static URL loadExternalConfig(String externalConfig) {
        try {
            URL url = Paths.get(externalConfig).toUri().toURL();

            url.openStream().close(); // Test URL to see if it exists

            return url;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load " + externalConfig, ex);
        }
    }

//    private Map<String,String> getMap(){
//        if(this.map == null ){
//            this.map = new HashMap<>();
//            Config cfg = ConfigProvider.getConfig();
//            fileuri = cfg.getOptionalValue(KEY_FILE_URI, String.class).orElse(DEFAULT_FILE_URI);
//            try {
//                log.info("Loading [file] MicroProfile ConfigSource");
//                Properties p = new Properties();
//                p.load(new FileInputStream(fileuri));
//                
//                for (final String name: p.stringPropertyNames()){
//                    this.map.put(name, p.getProperty(name));
//                }
//            } catch (IOException ex) {
//                log.log(Level.SEVERE, "Can not load properties file [{0}] due to [{1}]", new Object[]{fileuri, ex.getMessage()});
//            } 
//        }
//        return this.map;
//    }
//    

}
