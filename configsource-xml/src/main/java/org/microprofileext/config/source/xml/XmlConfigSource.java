package org.microprofileext.config.source.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.eclipse.microprofile.config.Config;
import org.microprofileext.config.source.base.file.AbstractUrlBasedSource;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Xml config source
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
public class XmlConfigSource extends AbstractUrlBasedSource {
    
    private static final Logger log = Logger.getLogger(XmlConfigSource.class.getName());
    
    @Override
    protected String getFileExtension() {
        return "xml";
    }
    
    @Override
    protected Map<String,String> toMap(final InputStream inputStream){
        try {
            InputSource inputSource = new InputSource(inputStream);
            return parse(inputSource);
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            log.log(Level.WARNING, "Could not create properties from XML [{0}]", ex.getMessage());
            return new HashMap<>();
        }
    }
    
    private Map<String, String> parse(InputSource inputSource) throws SAXException, IOException, ParserConfigurationException {
        final Handler handler = new Handler(getConfig(),super.getKeySeparator());
        SAXParserFactory.newInstance().newSAXParser().parse(inputSource, handler);
        return handler.result;
    }

    private class Handler extends DefaultHandler {
        private final StringBuilder valuebuffer = new StringBuilder();
        private final List<String> keybuffer = new LinkedList<>();
        private final Map<String, String> result = new HashMap<>();
        
        private final boolean ignoreRoot;
        private final String keySeparator;
        private int depth = -1;
        
        public Handler(Config cfg,String keySeparator){
            this.ignoreRoot = cfg.getOptionalValue("configsource.xml.ignoreRoot", Boolean.class).orElse(true);
            this.keySeparator = keySeparator;
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            depth++;
            if (this.depth == 0 && ignoreRoot) {
                // Ignoring root
            }else{
                keybuffer.add(qName);
            }
        }
        
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            final String value = valuebuffer.toString().trim();
            
            String key = String.join(keySeparator, keybuffer);
            if (!value.isEmpty()){
                if(result.containsKey(key)){
                    result.put(key, addToList(result.get(key),value));
                }else{
                    result.put(key, value.trim());
                }
            }
            valuebuffer.setLength(0);
            keybuffer.remove(qName);
            depth--;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            valuebuffer.append(ch, start, length);
        }
    }

    private String addToList(String existing,String newElement){
        if(newElement.contains(COMMA))newElement = newElement.replaceAll(COMMA, "\\\\,"); // Escape comma
        
        String[] split = existing.split(COMMA);
        List<String> l = new ArrayList<>(Arrays.asList(split));
        l.add(newElement);
        
        String join = String.join(COMMA, l);
        return join;
    }
    
    private static final String COMMA = ",";
}