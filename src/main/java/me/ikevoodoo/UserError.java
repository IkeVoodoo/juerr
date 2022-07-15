package me.ikevoodoo;

import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The UserError class is used to nicely print errors
 *
 * @see UserError#from(String)
 * @see UserError#intoUserError(Throwable)
 * @see UserError#fromStacktrace(Throwable)
 * @see UserError#UserError(String)
 * */
@SuppressWarnings("unused")
public class UserError {

    private final String message;
    private final List<String> help;
    private final List<String> reasons;

    /**
     * Create a new UserError
     *
     * @param message The message you want to print
     * */
    public UserError(String message) {
        this.message = message;
        this.help = new ArrayList<>();
        this.reasons = new ArrayList<>();
    }

    /**
     * Sets UserError to be the current thread's uncaught exception handler
     * */
    public static void setExceptionHandler() {
        setExceptionHandler(Thread.currentThread());
    }

    /**
     * Sets UserError to be the thread's uncaught exception handler
     *
     * @param thread The thread
     * */
    public static void setExceptionHandler(Thread thread) {
        thread.setUncaughtExceptionHandler((t, e) -> fromStacktrace(e).printAll(String.format("[%s] Uncaught: ", t.getName())));
    }

    /**
     * Create a new UserError from an exception if thrown
     *
     * @param runnable A runnable, it may throw an exception
     * @return An optional of UserError, if the runnable generated an exception this will be populated with a UserError
     * */
    public static Optional<UserError> from(ExceptionRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            return Optional.of(from(throwable.getLocalizedMessage()));
        }

        return Optional.empty();
    }

    /**
     * Create a new UserError from a message
     *
     * @param message The message you want to print
     * @return An instance of UserError
     * */
    public static UserError from(String message) {
        return new UserError(message);
    }

    /**
     * Create a new UserError from a Throwable
     *
     * @param throwable The throwable you want to print
     *                  Takes the localized message from the throwable
     * @return An instance of UserError
     * */
    public static UserError intoUserError(Throwable throwable) {
        return new UserError(throwable.getLocalizedMessage());
    }

    /**
     * Create a new UserError from an exception if thrown, generated reasons and help
     *
     * @param runnable A runnable, it may throw an exception
     * @return An optional of UserError, if the runnable generated an exception this will be populated with a UserError
     * */
    public static Optional<UserError> fromStacktrace(ExceptionRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            return Optional.of(fromStacktrace(throwable));
        }

        return Optional.empty();
    }

    /**
     * Create a new UserError from an Exception and generate reasons and help
     *
     * @param throwable The throable you want to print
     * @return An instance of UserError
     * */
    public static UserError fromStacktrace(Throwable throwable) {
        UserError error = new UserError(throwable.getLocalizedMessage());
        StackTraceError stackTraceError = new StackTraceError(error);

        stackTraceError.apply(throwable);

        return error;
    }

    /**
     * Print the UserError to the System.err PrintStream
     *
     * @param prefix The message prefix
     *               Added before the message
     * @return The current UserError
     * */
    public UserError printAll(String prefix) {
        return this.printAll(System.err, prefix);
    }

    /**
     * Print the UserError to a specified PrintStream
     *
     * @param prefix The message prefix
     *               Added before the message
     * @return The current UserError
     * */
    public UserError printAll(PrintStream stream, String prefix) {
        this.print(stream, prefix);
        return this;
    }

    /**
     * Add a help line
     *
     * @return The current UserError
     * */
    public UserError addHelp(String help) {
        this.help.add(help);
        return this;
    }

    /**
     * Add a reason line
     *
     * @return The current UserError
     * */
    public UserError addReason(String reason) {
        this.reasons.add(reason);
        return this;
    }

    /**
     * Get the current message
     *
     * @return The current message
     * */
    public String message() {
        return this.message;
    }

    /**
     * Get all the reasons
     *
     * @return The list of reasons
     * */
    public List<String> reasons() {
        return this.reasons;
    }

    /**
     * Get all the reasons
     *
     * @return The list of help lines
     * */
    public List<String> help() {
        return this.help;
    }

    /**
     * Internal use only, prints the UserError to a PrintStream
     *
     * @param stream The PrintStream to print to
     * @param prefix The prefix to prepend to the message
     * */
    private void print(PrintStream stream, String prefix) {
        stream.printf("%s%s\n", prefix, this.message);
        this.printList(stream, this.reasons, " - caused by: ", "     |        ");
        this.printList(stream, this.help, " + help: ", "     |   ");
    }

    /**
     * Internal use only, prints a list to a PrinStream
     *
     * @param stream The PrintStream to print to
     * @param list The list to print
     * @param prefix The prefix to prepend to the message
     * @param joiner The prefix for all messages after the first one
     * */
    private void printList(PrintStream stream, List<String> list, String prefix, String joiner) {
        if (list.isEmpty()) return;
        stream.printf("%s%s\n", prefix, list.get(0));
        list.subList(1, list.size()).forEach(entry -> stream.printf("%s%s\n", joiner, entry));
    }

}

/**
 * An interface used to run code and catch all throwable errors
 * */
interface ExceptionRunnable {
    void run() throws Throwable;
}

class StackTraceError {
    private static final Pattern NPE_EXTRACTOR = Pattern.compile("Cannot invoke \"([^\"]+)\" because \"([^\"]+)\" is null");
    private static final Pattern CLASS_EXTRACTOR = Pattern.compile(".*(?=\\.)");
    private static final Pattern CLASS_NAME_EXTRACTOR = Pattern.compile("(?<=\\.)[^.]*$");

    private final UserError error;

    protected StackTraceError(UserError error) {
        this.error = error;
    }

    void apply(Throwable throwable) {
        for (StackTraceElement element : throwable.getStackTrace()) {
            this.error.addReason(String.format("%s.%s(%s:%s)", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber()));
        }

        if (throwable instanceof NullPointerException) {
            this.applyNPE(throwable);
        }
    }

    private void applyNPE(Throwable throwable) {
        Matcher matcher = NPE_EXTRACTOR.matcher(throwable.getLocalizedMessage());
        if (matcher.matches()) {
            String attempted = matcher.group(1);
            String clazz = attempted;
            Matcher classMatcher = CLASS_EXTRACTOR.matcher(attempted);
            if (classMatcher.find()) {
                clazz = classMatcher.group();
                Matcher clazzNameMatcher = CLASS_NAME_EXTRACTOR.matcher(clazz);
                if (clazzNameMatcher.find()) {
                    clazz = clazzNameMatcher.group();
                }
            }
            String var = matcher.group(2);

            this.error.addHelp(String.format("Try checking if %s is not null with an if statement", var));
            this.error.addHelp(String.format("Try wrapping %s in a Optional<%s>", var, clazz));
            this.error.addHelp(String.format("Check where %s is assigned", var));
        }
    }
}