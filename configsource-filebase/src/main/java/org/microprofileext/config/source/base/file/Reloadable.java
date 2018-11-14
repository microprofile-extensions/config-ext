package org.microprofileext.config.source.base.file;

import java.net.URL;

/**
 * Marks a class as reloadable
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
public interface Reloadable {
    public void reload(URL url);
}
