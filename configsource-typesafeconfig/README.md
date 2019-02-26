[Back to config-ext](https://github.com/microprofile-extensions/config-ext/blob/master/README.md)

# TypeSafeConfig Config Source

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-typesafeconfig/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-consul)
[![Javadocs](https://www.javadoc.io/badge/org.microprofile-ext.config-ext/configsource-typesafeconfig.svg)](https://www.javadoc.io/doc/org.microprofile-ext.config-ext/configsource-consul)

Use [TypeSafe Config](https://github.com/lightbend/config) to get config values.

## Usage

```xml

    <dependency>
        <groupId>org.microprofile-ext.config-ext</groupId>
        <artifactId>configsource-typesafeconfig</artifactId>
        <version>XXXXXX</version>
        <scope>runtime</scope>
    </dependency>

```

## Configure options

You can disable the config source by setting this config:
    
    TypeSafeConfigConfigSource.enabled=false  

## Links
* https://microprofile.io/project/eclipse/microprofile-config
