package org.microprofileext.config.event;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.enterprise.util.AnnotationLiteral;

/**
 * Filter by config source
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Qualifier
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SourceFilter {
    String value();
    
    class SourceFilterLiteral extends AnnotationLiteral<SourceFilter> implements SourceFilter {
        private String name;

        public SourceFilterLiteral() {
        }

        public SourceFilterLiteral(String name) {
            this.name = name;
        }
        
        @Override
        public String value(){
            return this.name;
        }
    }
}