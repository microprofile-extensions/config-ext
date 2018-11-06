[Back to config-ext](https://github.com/microprofile-extensions/config-ext/blob/master/README.md)

# Memory config source

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-memory/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-memory)
[![Javadocs](https://www.javadoc.io/badge/org.microprofile-ext.config-ext/configsource-memory.svg)](https://www.javadoc.io/doc/org.microprofile-ext.config-ext/configsource-memory)

This source gets and sets values in memory. Useful when you want to change config during runtime.

## Usage

```xml

    <dependency>
        <groupId>org.microprofile-ext.config-ext</groupId>
        <artifactId>configsource-memory</artifactId>
        <version>XXXX</version>
    </dependency>

```

## Info

You can do this by using the REST API to change the config values:

```

    GET /microprofile-ext/memoryconfigsource/sources - list all config sources
    GET /microprofile-ext/memoryconfigsource/all - get all configurations
    GET /microprofile-ext/memoryconfigsource/key/{key} - get the configured value for {key}
    PUT /microprofile-ext/memoryconfigsource/key/{key} - set the value for {key}
    DELETE /microprofile-ext/memoryconfigsource/key/{key} - delete the configured value for {key}

```

## Events

This config source fires CDI Events on PUT and DELETE:

The CDI Event is a `MemoryConfigEvent` and contains the following fields: 

* String key
* String oldValue
* String newValue 
* Type type

There are 3 type: 

* NEW - When you create a new key and value (i.e. the key does not exist anywhere in any config source)
* OVERRIDE - When you override a value of an existing key (i.e. the key and value exist somewhere in a config source)
* REVERT - When you remove the value added to a NEW or OVERRIDE key (i.e revert to the state on startup)

### Observing events:

You can listen to all or some of these events, filtering by Type and/or key, example:

```java

    // Getting all memory config event
    public void all(@Observes MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "ALL: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Get only new values
    public void newValue(@Observes @EventType(Type.NEW) MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "NEW: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Get only override values
    public void overrideValue(@Observes @EventType(Type.OVERRIDE) MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "OVERRIDE: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Get only revert values
    public void revertValue(@Observes @EventType(Type.REVERT) MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "REVERT: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Getting all memory config event when key is some.key
    public void allForKey(@Observes @EventKey("some.key") MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "ALL for key [some.key]: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Getting all memory config event when key is some.key for new events
    public void newForKey(@Observes @EventType(Type.NEW) @EventKey("some.key") MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "NEW for key [some.key]: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Getting all memory config event when key is some.key for override events
    public void overrideForKey(@Observes @EventType(Type.OVERRIDE) @EventKey("some.key") MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "OVERRIDE for key [some.key]: Received a memory config event: {0}", memoryConfigEvent);
    }
    
    // Getting all memory config event when key is some.key for revert events
    public void revertForKey(@Observes @EventType(Type.REVERT) @EventKey("some.key") MemoryConfigEvent memoryConfigEvent){
        log.log(Level.SEVERE, "REVERT for key [some.key]: Received a memory config event: {0}", memoryConfigEvent);
    }

```

Note: You can filter by including the `@EventType`.

## Configure options

You can disable the config source by setting this config:
    
    MemoryConfigSource.enabled=false

![REST API](https://github.com/microprofile-extensions/config-ext/raw/master/configsource-memory/screenshot.png)
