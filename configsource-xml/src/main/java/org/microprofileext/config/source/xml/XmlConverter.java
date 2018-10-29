package org.microprofileext.config.source.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import lombok.Getter;
import lombok.extern.java.Log;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Convert xml format to Map
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 * TODO: Allow configuration to use attributes vs elements
 */
@Log
public class XmlConverter {
    
    @Getter
    private Map<String,String> properties = new TreeMap<>();
    
    public XmlConverter(InputStream in){
        this(in,true);
    }

    public XmlConverter(InputStream in, boolean ignoreRoot){
        try {
            InputSource inputSource = new InputSource(in);
            this.properties = parse(inputSource,ignoreRoot);
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            log.log(Level.WARNING, "Could not create properties from XML [{0}]", ex.getMessage());
        }
    }
    
    private Map<String, String> parse(InputSource inputSource,boolean ignoreRoot) throws SAXException, IOException, ParserConfigurationException {
        final Handler handler = new Handler(ignoreRoot);
        SAXParserFactory.newInstance().newSAXParser().parse(inputSource, handler);
        return handler.result;
    }

    private class Handler extends DefaultHandler {
        private final StringBuilder valuebuffer = new StringBuilder();
        private final List<String> keybuffer = new LinkedList<>();
        private final Map<String, String> result = new HashMap<>();
        
        private boolean ignoreRoot = true;
        private int depth = -1;
        
        public Handler(boolean ignoreRoot){
            this.ignoreRoot = ignoreRoot;
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
            
            String key = String.join(SEPARATOR, keybuffer);
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
        if(existing.startsWith(OPEN_BRACKET))existing = existing.replace(OPEN_BRACKET, EMPTY);
        if(existing.endsWith(CLOSE_BRACKET))existing = existing.replace(CLOSE_BRACKET, EMPTY);
        if(existing.contains(COMMA))existing = existing.replace(COMMA + SPACE, COMMA);
        existing = existing.trim() + COMMA + newElement.trim();
        List<String> l = Arrays.asList(existing.split(COMMA));
        return l.toString();
    }
    
    private static final String SEPARATOR = "."; // TODO: Allow this to be configured ?
    
    private static final String OPEN_BRACKET = "[";
    private static final String CLOSE_BRACKET = "]";
    private static final String COMMA = ",";
    private static final String EMPTY = "";
    private static final String SPACE = " ";
}