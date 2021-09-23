# http-router

Tree based http-router component for Java (>= v11)

### Usage example

See [JettyExample.java](src/test/java/com/ukarim/httprouter/example/JettyExample.java)

### Install

##### Option 1

Build from sources:

```shell
mvn clean install
```

##### Option 2

I've uploaded artifacts to my website in order to use in my personal projects.

Add the following configs to your pom.xml file:

```xml
<repositories>
    <repository>
        <id>ukarim</id>
        <name>ukarim</name>
        <url>https://ukarim.com/maven</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.ukarim</groupId>
        <artifactId>http-router</artifactId>
        <version>0.1.0</version> <!-- replace by latest version from https://github.com/ukarim/http-router/blob/main/pom.xml file -->
    </dependency>
</dependencies>
```

##### Option 3

Raise the issue and I will try to deploy the lib to the Maven central
