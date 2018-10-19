# Config API extension | Memory config source

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-memory/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-memory)
[![Javadocs](https://www.javadoc.io/badge/org.microprofile-ext.config-ext/configsource-memory.svg)](https://www.javadoc.io/doc/org.microprofile-ext.config-ext/configsource-memory)

This source gets and sets values in memory. Useful when you want to change config during runtime.

## Usage

```xml

    <dependency>
        <groupId>org.microprofile-ext.config-ext</groupId>
        <artifactId>configsource-memory</artifactId>
        <version>XXXX</version>
        <scope>runtime</scope>
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
## Configure options

You can disable the config source by setting this config:
    
    MemoryConfigSource.enabled=false

![REST API](https://github.com/microprofile-extensions/config-ext/raw/master/configsource-memory/screenshot.png)