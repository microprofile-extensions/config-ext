[Back to config-ext](https://github.com/microprofile-extensions/config-ext/blob/master/README.md)

# Properties Config Source

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-properties/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-properties)
[![Javadocs](https://www.javadoc.io/badge/org.microprofile-ext.config-ext/configsource-properties.svg)](https://www.javadoc.io/doc/org.microprofile-ext.config-ext/configsource-properties)

This source gets values from some properties file(s).

## Usage

```xml

    <dependency>
        <groupId>org.microprofile-ext.config-ext</groupId>
        <artifactId>configsource-properties</artifactId>
        <version>XXXXX</version>
        <scope>runtime</scope>
    </dependency>

```

## Example:

```properties
    
    somekey=somevalue
    location.protocol=http
    location.host=localhost
    location.port=8080
    location.path=/some/path
    location.jedis=[Yoda, Qui-Gon Jinn, Obi-Wan Kenobi, Luke Skywalker]
    
```

## Configure options

### Url(s)

By default the config source will look for a file called `application.properties`. You can set the location(s) of the files:

    configsource.properties.url=<here the url(s)>

example:

    configsource.properties.url=file:/tmp/myconfig.properties

You can also add more than one location by comma-separating the location:

    configsource.properties.url=file:/tmp/myconfig.json,http://localhost/myconfig.properties

The latest files will override properties in previous files. As example, if using above configuration, property `foo=bar` in `file:/tmp/myconfig.properties` will be override if it's added to `http://localhost/myconfig.properties`.

### Key separator

By default the separator used in the key is a DOT (.) example:

```property
    
    "location.protocol": "http"
```

You can change this by setting `configsource.properties.keyseparator` to the desired separator, example:

    configsource.properties.keyseparator=_

will create:

```property
    
    "location_protocol": "http"
```
