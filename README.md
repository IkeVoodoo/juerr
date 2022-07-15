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
## WIth multiple arguments
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