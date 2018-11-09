[Back to config-ext](https://github.com/microprofile-extensions/config-ext/blob/master/README.md)

# Config events

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/config-events/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/config-events)
[![Javadocs](https://www.javadoc.io/badge/org.microprofile-ext.config-ext/config-events.svg)](https://www.javadoc.io/doc/org.microprofile-ext.config-ext/config-events)

Util library for config sources that fire events on changes

## Usage

```xml

    <dependency>
        <groupId>org.microprofile-ext.config-ext</groupId>
        <artifactId>config-events</artifactId>
        <version>XXXX</version>
    </dependency>

```

## The event

The CDI Event is a `ChangeEvent` and contains the following fields: 

* String key
* Optional (String) oldValue
* String newValue 
* Type type

There are 3 types: 

* NEW - When you create a new key and value (i.e. the key does not exist anywhere in any config source)
* OVERRIDE - When you override a value of an existing key (i.e. the key and value exist somewhere in a config source)
* REVERT - When you remove the value added to a NEW or OVERRIDE key (i.e revert to the state on startup)

### Observing events:

You can listen to all or some of these events, filtering by Type and/or key and/or Source, example:

```java

    // Getting all memory config event
    public void all(@Observes ChangeEvent changeEvent){
        log.log(Level.SEVERE, "ALL: Received a config change event: {0}", changeEvent);
    }
    
    // Get only new values
    public void newValue(@Observes @TypeFilter(Type.NEW) ChangeEvent changeEvent){
        log.log(Level.SEVERE, "NEW: Received a config change event: {0}", changeEvent);
    }
    
    // Get only override values
    public void overrideValue(@Observes @TypeFilter(Type.OVERRIDE) ChangeEvent changeEvent){
        log.log(Level.SEVERE, "OVERRIDE: Received a config change event: {0}", changeEvent);
    }
    
    // Get only revert values
    public void revertValue(@Observes @TypeFilter(Type.REVERT) ChangeEvent changeEvent){
        log.log(Level.SEVERE, "REVERT: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config event when key is some.key
    public void allForKey(@Observes @KeyFilter("some.key") ChangeEvent changeEvent){
        log.log(Level.SEVERE, "ALL for key [some.key]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config event when key is some.key for new events
    public void newForKey(@Observes @TypeFilter(Type.NEW) @KeyFilter("some.key") ChangeEvent changeEvent){
        log.log(Level.SEVERE, "NEW for key [some.key]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config event when key is some.key for override events
    public void overrideForKey(@Observes @TypeFilter(Type.OVERRIDE) @KeyFilter("some.key") ChangeEvent changeEvent){
        log.log(Level.SEVERE, "OVERRIDE for key [some.key]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config event when key is some.key for revert events
    public void revertForKey(@Observes @TypeFilter(Type.REVERT) @KeyFilter("some.key") ChangeEvent changeEvent){
        log.log(Level.SEVERE, "REVERT for key [some.key]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config events for a certain source
    public void allForSource(@Observes @SourceFilter("MemoryConfigSource") ChangeEvent changeEvent){
        log.log(Level.SEVERE, "ALL for source [MemoryConfigSource]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config events for a certain source
    public void allForSourceAndKey(@Observes @SourceFilter("MemoryConfigSource") @KeyFilter("some.key")  ChangeEvent changeEvent){
        log.log(Level.SEVERE, "ALL for source [MemoryConfigSource] and for key [some.key]: Received a config change event: {0}", changeEvent);
    }
    
    // Getting all config events for a certain source
    public void overrideForSourceAndKey(@Observes @TypeFilter(Type.OVERRIDE) @SourceFilter("MemoryConfigSource") @KeyFilter("some.key")  ChangeEvent changeEvent){
        log.log(Level.SEVERE, "OVERRIDE for source [MemoryConfigSource] and for key [some.key]: Received a config change event: {0}", changeEvent);
    }

```

Note: You can filter by including the `@TypeFilter` and/or the `@KeyFilter` and/or the `@SourceFilter`.

## Implementing this for your own Config source

An example of a source that uses this is [Memory Config source](https://github.com/microprofile-extensions/config-ext/blob/master/configsource-memory/README.md)

When a config value change we fire an event:

```java

    private void fireEvent(Type type,String key,String newValue){
        String oldValue = config.getOptionalValue(key, String.class).orElse(null);
        
        if(type==null){
            if(oldValue==null || oldValue.isEmpty()){
                type = Type.NEW;
            }else{
                type = Type.OVERRIDE;
            }
        }
        
        List<Annotation> annotationList = new ArrayList<>();
        annotationList.add(new TypeFilter.TypeFilterLiteral(type));
        annotationList.add(new KeyFilter.KeyFilterLiteral(key));
        annotationList.add(new SourceFilter.SourceFilterLiteral(MemoryConfigSource.NAME));
        
        Event<ChangeEvent> selected = broadcaster.select(annotationList.toArray(new Annotation[annotationList.size()]));
        selected.fire(new ChangeEvent(type, key, getOptionalOldValue(oldValue), newValue));
    }
    
    private Optional<String> getOptionalOldValue(String oldValue){
        if(oldValue==null || oldValue.isEmpty())return Optional.empty();
        return Optional.of(oldValue);
    }

```


