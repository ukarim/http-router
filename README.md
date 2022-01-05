# http-router

Tree based http-router component for Java (>= v11)

### Usage example

```java
import com.ukarim.httprouter.*;

// create and setup http router instance
var router = new HttpRouter<HttpHandler>();
router.get("/users", new UsersListHandler());
router.get("/users/:login", new UserProfileHandler());
router.post("/users", new UserCreationHandler());

// get a handler for specific http request
var routerMatch = router.match("GET", "/users/ukarim");
var params = routerMatch.getParams(); // contains path variables
var httpHandler = routerMatch.getHandler();
```

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
        <version>0.1.1</version> <!-- replace by latest version from https://github.com/ukarim/http-router/blob/main/pom.xml file -->
    </dependency>
</dependencies>
```

##### Option 3

Raise the issue and I will try to deploy the lib to the Maven central
