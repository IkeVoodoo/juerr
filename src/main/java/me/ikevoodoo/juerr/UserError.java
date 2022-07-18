package me.ikevoodoo.juerr;

import me.ikevoodoo.juerr.traces.StackTraceError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private static final PrintStreamPrinter streamPrinter = new PrintStreamPrinter(System.err);

    private final String message;
    private final List<UserErrorEntry> help;
    private final List<UserErrorEntry> reasons;

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
     * Sets UserError to be the thread uncaught error handler for all threads
     * */
    public static void setAllExceptionHandler() {
        Thread.getAllStackTraces().forEach((thread, stackTraceElements) -> setExceptionHandler(thread));
    }

    /**
     * Sets UserError to be the thread's uncaught exception handler
     *
     * @param thread The thread
     * */
    public static void setExceptionHandler(Thread thread) {
        thread.setUncaughtExceptionHandler((t, e) ->
                fromStacktrace(e).printAll(String.format("[Thread %s] Uncaught: ", t.getName())));
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
        return this.printAll(streamPrinter, prefix);
    }

    /**
     * Print the UserError to a specified PrintStream
     *
     * @param prefix The message prefix
     *               Added before the message
     * @return The current UserError
     * */
    public UserError printAll(Printer<?> printer, String prefix) {
        this.print(printer, prefix);
        return this;
    }

    /**
     * Add a help line
     *
     * @return The current UserError
     * */
    public UserError addHelp(String help) {
        return this.addHelp(UserErrorEntry.from(help.split("\n")));
    }

    /**
     * Add a reason line
     *
     * @return The current UserError
     * */
    public UserError addReason(String reason) {
        return this.addReason(UserErrorEntry.from(reason.split("\n")));
    }

    /**
     * Add a help line
     *
     * @return The current UserError
     * */
    public UserError addHelp(UserErrorEntry help) {
        this.help.add(help);
        return this;
    }

    /**
     * Add a reason line
     *
     * @return The current UserError
     * */
    public UserError addReason(UserErrorEntry reason) {
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
    public List<UserErrorEntry> reasons() {
        return this.reasons;
    }

    /**
     * Get all the reasons
     *
     * @return The list of help lines
     * */
    public List<UserErrorEntry> help() {
        return this.help;
    }

    /**
     * Internal use only, prints the UserError to a PrintStream
     *
     * @param printer The Printer to print to
     * @param prefix The prefix to prepend to the message
     * */
    private void print(Printer<?> printer, String prefix) {
        printer.printfln("%s%s", prefix, this.message);
        this.printList(printer, this.reasons, " - caused by: ", "     |        ");
        this.printList(printer, this.help, " + help: ", "     |   ");
    }

    /**
     * Internal use only, prints a list to a PrinStream
     *
     * @param printer The Printer to print to
     * @param list The list to print
     * @param prefix The prefix to prepend to the message
     * @param joiner The prefix for all messages after the first one
     * */
    private void printList(Printer<?> printer, List<UserErrorEntry> list, String prefix, String joiner) {
        if (list.isEmpty()) return;

        UserErrorEntry first = findFirstWithLines(list);
        if (first == null) return;
        printer.printfln("%s%s", prefix, first.lines().get(0));
        for (int i = 1; i < first.lines().size(); i++) {
            printer.printfln("%s%s", joiner, first.lines().get(i));
        }
        int index = list.indexOf(first) + 1;

        list.subList(index, list.size()).forEach(entry ->
                entry.lines().forEach(line ->
                        printer.printfln("%s%s", joiner, line)));

        /*
        UserErrorEntry first = list.get(0);

        list.get(0).lines().forEach(line -> printer.printfln("%s%s", prefix, line));

        list.subList(1, list.size()).forEach(entry ->
                entry.lines().forEach(line ->
                        printer.printfln("%s%s", joiner, line)));*/
    }

    private UserErrorEntry findFirstWithLines(List<UserErrorEntry> list) {
        for (UserErrorEntry entry : list)
            if (entry.lines().size() > 0)
                return entry;
        return null;
    }

}

/**
 * An interface used to run code and catch all throwable errors
 * */
interface ExceptionRunnable {
    void run() throws Throwable;
}