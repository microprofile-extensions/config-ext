# Json Config Source

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-json/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.microprofile-ext.config-ext/configsource-json)
[![Javadocs](https://www.javadoc.io/badge/org.microprofile-ext.config-ext/configsource-json.svg)](https://www.javadoc.io/doc/org.microprofile-ext.config-ext/configsource-json)

This source gets values from some json file(s).

## Usage

```xml

    <dependency>
        <groupId>org.microprofile-ext.config-ext</groupId>
        <artifactId>configsource-json</artifactId>
        <version>XXXXX</version>
        <scope>runtime</scope>
    </dependency>

```

## Configure options

By default the config source will look for a file called `application.json`. You can set the location(s) of the files:

    configsource.json.url=<here the url(s)>

example:

    configsource.json.url=file:/tmp/myconfig.json

You can also add more than one location by comma-separating the location:

    configsource.json.url=file:/tmp/myconfig.json,http://localhost/mycongig.json

The latest files will override properties in previous files. As example, if using above configuration, property `foo=bar` in `file:/tmp/myconfig.json` will be override if it's added to `http://localhost/mycongig.json`.

## Example:

```json
    
    {
	"location": {
		"protocol": "http",
		"host": "localhost",
		"port": 8080,
		"path": "/some/path",
		"jedis": [
			"Yoda",
			"Qui-Gon Jinn",
			"Obi-Wan Kenobi",
			"Luke Skywalker"
		]
	}
    }
    
```

will create the following properties:

```property
    
    "location.protocol": "http"
    "location.host": "localhost"
    "location.port": "8080"
    "location.path": "/some/path"
    "location.jedis": "[Yoda, Qui-Gon Jinn, Obi-Wan Kenobi, Luke Skywalker]"

```
