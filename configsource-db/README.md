[![Build Status](https://travis-ci.org/rikcarve/mp-config-db.svg?branch=master)](https://travis-ci.org/rikcarve/mp-config-db)
[![codecov](https://codecov.io/gh/rikcarve/mp-config-db/branch/master/graph/badge.svg)](https://codecov.io/gh/rikcarve/mp-config-db)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.carve/mp-config-db/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/ch.carve/mp-config-db/)

# mp-config-db
A eclipse microprofile config (1.2) extension which uses a database as source.

## Overview
The eclipse microprofile config framework is a simple yet powerful configuration framework for Java EE. But most implementations only provide the system/env properties or property files as configuration source. This small library provides an ConfigSource implementation which reads the values from the default datasource. For performance reasons, the config values are cached.

## Add dependency
```xml
        <dependency>
            <groupId>ch.carve</groupId>
            <artifactId>mp-config-db</artifactId>
            <version>0.2</version>
        </dependency>
```

## Configuration
Currently there are 5 values you can configure, either through Java system properties or environment variables:
* **mp-config-db.datasource** override default datasource by setting JNDI name of the datasource
* **mp-config-db.table** table name for configuration records, default value is "configuration"
* **mp-config-db.keyColumn** name of the column containing the key, default value is "key"
* **mp-config-db.valueColumn** name of the column containing the value, default value is "value"
* **mp-config-db.validity** how long to cache values (in seconds), default is 30s

## Hint
Use memory config source from [microprofile-extensions](https://github.com/microprofile-extensions/config-ext/tree/master/configsource-memory) to get a REST interface and the possibility to change values on the fly (in-memory)

## Links
* https://microprofile.io/project/eclipse/microprofile-config
* https://github.com/rikcarve/consulkv-maven-plugin
* https://github.com/rikcarve/mp-config-consul
* https://github.com/microprofile-extensions

