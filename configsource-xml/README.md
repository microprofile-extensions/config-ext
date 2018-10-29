# Xml Config Source

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-xml/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-xml)
[![Javadocs](https://www.javadoc.io/badge/org.microprofile-ext.config-ext/configsource-xml.svg)](https://www.javadoc.io/doc/org.microprofile-ext.config-ext/configsource-xml)

This source gets values from some xml(s) file.

## Usage

```xml

    <dependency>
        <groupId>org.microprofile-ext.config-ext</groupId>
        <artifactId>configsource-xml</artifactId>
        <version>XXXXX</version>
        <scope>runtime</scope>
    </dependency>

```

## Configure options

By default the config source will look for a file called `application.xml`. You can set the location(s) of the files:

    configsource.xml.url=<here the url(s)>

example:

    configsource.xml.url=file:/tmp/myconfig.xml

You can also add more than one location by comma-separating the location:

    configsource.xml.url=file:/tmp/myconfig.xml,http://localhost/mycongig.xml

The latest files will override properties in previous files. As example, if using above configuration, property `foo=bar` in `file:/tmp/myconfig.xml` will be override if it's added to `http://localhost/mycongig.xml`.

## Example:

```xml

    <?xml version="1.0" encoding="UTF-8" ?>
    <root>
        <somekey>somevalue</somekey>
        <location>
            <protocol>http</protocol>
            <host>localhost</host>
            <port>8080</port>
            <path>/some/path</path>
            <jedis>Yoda</jedis>
            <jedis>Qui-Gon Jinn</jedis>
            <jedis>Obi-Wan Kenobi</jedis>
            <jedis>Luke Skywalker</jedis>
        </location>
    </root>
```

will create the following properties:

```property
    
    "somekey": "somevalue"
    "location.protocol": "http"
    "location.host": "localhost"
    "location.port": "8080"
    "location.path": "/some/path"
    "location.jedis": "[Yoda, Qui-Gon Jinn, Obi-Wan Kenobi, Luke Skywalker]"

```
