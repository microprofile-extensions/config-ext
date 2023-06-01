package org.microprofileext.config.event;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import jakarta.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import jakarta.enterprise.util.AnnotationLiteral;

/**
 * filter by change type
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
@Qualifier
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TypeFilter {
    Type value();
    
    class TypeFilterLiteral extends AnnotationLiteral<TypeFilter> implements TypeFilter {
        private Type type;

        public TypeFilterLiteral() {
        }

        public TypeFilterLiteral(Type type) {
            this.type = type;
        }
        
        @Override
        public Type value(){
            return this.type;
        }
    }
}