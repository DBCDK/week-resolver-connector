weekresolver-connector
=============

A Java library providing a weekresolver client.

### usage

Add the dependency to your Maven pom.xml

```xml
<dependency>
  <groupId>dk.dbc</groupId>
  <artifactId>weekresolver-connector</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
 In your Java code

```java
import dk.dbc.weekresolver.weekresolverConnector;
import javax.inject.Inject;
...

// Assumes environment variable WEEKRESOLVER_SERVICE_URL
// is set to the base URL of the weekresolver provider service.
@Inject
WeekresolverConnector connector;

WeekresolverResult r = connector.getWeekCode("bpf", LocalDate.parse("2019-1010")




```

### development

**Requirements**

To build this project JDK 1.8 or higher and Apache Maven is required.

### License

Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3.
See license text in LICENSE.txt