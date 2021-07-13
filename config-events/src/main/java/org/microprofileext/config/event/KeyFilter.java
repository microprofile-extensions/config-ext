package org.microprofileext.config.event;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.enterprise.util.AnnotationLiteral;

/**
 * Filter the event on the key
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Qualifier
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KeyFilter {
    String value();
    
    class KeyFilterLiteral extends AnnotationLiteral<KeyFilter> implements KeyFilter {
        private String key;

        public KeyFilterLiteral(){
            
        }
        
        public KeyFilterLiteral(String key) {
            this.key = key;
        }
        
        @Override
        public String value(){
            return this.key;         
        }
    }
}