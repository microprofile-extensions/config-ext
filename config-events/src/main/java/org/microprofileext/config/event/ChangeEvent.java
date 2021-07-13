package org.microprofileext.config.event;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * an event on config element
 * @author <a href="mailto:phillip.kruger@phillip-kruger.com">Phillip Kruger</a>
 */
public class ChangeEvent implements Serializable {

    private Type type;
    private String key;
    private Optional<String> oldValue;
    private String newValue;   
    private String fromSource;
    
    public ChangeEvent(Type type, String key, Optional<String> oldValue, String newValue, String fromSource) {
        this.type = type;
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.fromSource = fromSource;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Optional<String> getOldValue() {
        return oldValue;
    }

    public void setOldValue(Optional<String> oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getFromSource() {
        return fromSource;
    }

    public void setFromSource(String fromSource) {
        this.fromSource = fromSource;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.type);
        hash = 79 * hash + Objects.hashCode(this.key);
        hash = 79 * hash + Objects.hashCode(this.oldValue);
        hash = 79 * hash + Objects.hashCode(this.newValue);
        hash = 79 * hash + Objects.hashCode(this.fromSource);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChangeEvent other = (ChangeEvent) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.newValue, other.newValue)) {
            return false;
        }
        if (!Objects.equals(this.fromSource, other.fromSource)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.oldValue, other.oldValue)) {
            return false;
        }
        return true;
    }
    
    
}
