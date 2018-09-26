# Config API extension

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-memory/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-memory)
[![Javadocs](https://www.javadoc.io/badge/org.microprofile-ext.config-ext/configsource-memory.svg)](https://www.javadoc.io/doc/org.microprofile-ext.config-ext/configsource-memory)

This extension gives you some extra configuration sources and some extra converters.

## Usage

    <dependency>
        <groupId>org.microprofile-ext.config-ext</groupId>
        <artifactId>configsource-memory</artifactId>
        <version>XXXX</version>
        <scope>runtime</scope>
    </dependency>

## Info

This source gets and sets values in memory. Useful when you want to change config during runtime.

You can do this by using the REST API to change the config values:

    GET /microprofile-ext/memoryconfigsource/sources - list all config sources
    GET /microprofile-ext/memoryconfigsource/all - get all configurations
    GET /microprofile-ext/memoryconfigsource/key/{key} - get the configured value for {key}
    PUT /microprofile-ext/memoryconfigsource/key/{key} - set the value for {key}
    DELETE /microprofile-ext/memoryconfigsource/key/{key} - delete the configured value for {key}

![REST API](https://raw.githubusercontent.com/phillip-kruger/microprofile-extensions/master/config-ext/screenshot.png)