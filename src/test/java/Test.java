import me.ikevoodoo.juerr.ProjectInfo;
import me.ikevoodoo.juerr.UserError;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class Test {

    public static void main(String[] args) throws IOException, URISyntaxException {
        UserError.setExceptionHandler();
        ProjectInfo.load(Test.class);

        A.tryButFail();
        //Files.readAllLines(null);

        /**
         * Goal:
         *
         * [Thread main] Uncaught: null
         *  - caused by: java.nio.file.Files.provider(Files.java:97)
         *      |        java.nio.file.Files.newInputStream(Files.java:152)
         *      |        java.nio.file.Files.newBufferedReader(Files.java:2784)
         *      |        java.nio.file.Files.readAllLines(Files.java:3202)
         *      |        java.nio.file.Files.readAllLines(Files.java:3242)
         *      |        Test.main(Test.java:11)
         *  + help: Test.main(String[]) tried to call Files.readAllLines(null) on line 11
         *      |   Try passing in a Path instance to Files.readAllLines(Path)
         *      |
         *      |   --- SNIPPET ---
         *      |   10. Files.readAllLines(Path.of("..."));
         *      |   --- SNIPPET ---
         *      |
         *      |
         *      |   Surround with a try-catch
         *      |
         *      |   --- SNIPPET ---
         *      |   10.  try {
         *      |   11.      Files.readAllLines(null);
         *      |   12.  } catch (NullPointerException exception) {
         *      |   13.      // Handle the exception
         *      |   14.      return;
         *      |   15.  }
         *      |   --- SNIPPET ---
         *      |
         *      |
         *
         * */
    }

}

class A {

    public static void tryButFail() {
        new B().getC().getD().execute();
    }

    public static class B {

        public C getC() {
            return new C();
        }

        public static class C {

            public D getD() {
                return new D();
            }

            public static class D {
                public void execute() {
                    throw new RuntimeException("Exception");
                }

            }


        }



    }


}