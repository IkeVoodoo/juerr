## Juerr
`Juerr` is a single-file Java port of the [uerr](https://crates.io/crates/uerr) crate, it provides stunning visual error handling.

# Showcase
Using the code below, we can display a simple error and show it to the user.

```java

import me.ikevoodoo.UserError;

public class ErrorTest {
    public static void main(String[] args) {
        UserError.from("could not open file")
                .addReason("The system cannot find the file specified.")
                .addHelp("Does this file exist?")
                .printAll("juerr/error: ");
    }
}
```
### Output
```
juerr/error: could not open file
 - caused by: The system cannot find the file specified.
 + help: Does this file exist?
```
## With multiple arguments
```java
import me.ikevoodoo.UserError;

public class ErrorTest {
    public static void main(String[] args) {
        UserError.from("could not open file")
                .addReason("The system cannot find the file specified.")
                .addReason("Fillter reason")
                .addHelp("Does this file exist?")
                .addHelp("Filler help.")
                .printAll("program.jar: ");
    }
}
```
### Output
```
program.jar: could not open file
 - caused by: The system cannot find the file specified.
     |        Filler reason.
 + help: Does this file exist?
     |   Filler help.
```
## Running and auto catching exceptions
```java
import me.ikevoodoo.UserError;

import java.io.IOException;
import java.nio.file.Files;

public class ErrorTest {
    public static void main(String[] args) {
        UserError.from(ErrorTest::myFunction).ifPresent(err -> err.printAll("myFunction: "));
    }

    public static void myFunction() throws IOException {
        Files.readAllBytes(null);
    }
}
```
### Output (if an exception was thrown)
```
myFunction: Any error that was raised
```
## Running and generating error (WIP)
```java
import me.ikevoodoo.UserError;

import java.io.IOException;
import java.nio.file.Files;

public class ErrorTest {
    public static void main(String[] args) {
        UserError.fromStacktrace(ErrorTest::myFunction).ifPresent(err -> err.printAll("myFunction: "));
    }

    public static void myFunction() throws IOException {
        Files.readAllBytes(null);
    }
}
```
### Output (if an exception was thrown)
```java
Could not read file: Cannot invoke "java.nio.file.Path.getFileSystem()" because "path" is null
 - caused by: java.nio.file.Files.provider(Files.java:105)
     |        java.nio.file.Files.newByteChannel(Files.java:380)
     |        java.nio.file.Files.newByteChannel(Files.java:432)
     |        java.nio.file.Files.readAllBytes(Files.java:3289)
     |        me.ikevoodoo.ErrorTest.main(UserError.java:178)
 + help: Try checking if path is not null
     |   Try wrapping path in a Optional<Path>
     |   Check where path is assigned
```
## Uncaught exceptions
```java
import me.ikevoodoo.UserError;

import java.io.IOException;
import java.nio.file.Files;

public class ErrorTest {
    public static void main(String[] args) throws IOException {
        UserError.setExceptionHandler(); // Sets the thread's uncaught exception handler
        
        Files.readAllBytes(null);
    }
}
```
### Output
```
[Thread main] Uncaught: null
 - caused by: java.nio.file.Files.provider(Files.java:97)
     |        java.nio.file.Files.newByteChannel(Files.java:361)
     |        java.nio.file.Files.newByteChannel(Files.java:407)
     |        java.nio.file.Files.readAllBytes(Files.java:3152)
     |        me.ikevoodoo.ErrorTest.main(Test.java:11)
```


# Maven
```xml
coming soon
```

# Gradle
```
coming soon
```