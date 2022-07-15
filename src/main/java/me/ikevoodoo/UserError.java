package me.ikevoodoo;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The UserError class is used to nicely print errors
 *
 * @see UserError#from(String)
 * @see UserError#intoUserError(Error)
<<<<<<< HEAD
 * @see UserError#intoUserError(Throwable)
=======
 * @see UserError#intoUserError(Exception) 
>>>>>>> 0560a8a5ca0a7573fdc43ea72a28bf86bc3f5762
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
     * Create a new UserError from a message
     *
     * @param message The message you want to print
     * @return An instance of UserError
     * */
    public static UserError from(String message) {
        return new UserError(message);
    }

    /**
     * Create a new UserError from an Error
     *
     * @param error The error you want to print
     *              Takes the localized message from the error
     * @return An instance of UserError
     * */
    public static UserError intoUserError(Error error) {
        return new UserError(error.getLocalizedMessage());
    }

    /**
     * Create a new UserError from an Exception
     *
     * @param exception The error you want to print
     *                  Takes the localized message from the exception
     * @return An instance of UserError
     * */
    public static UserError intoUserError(Throwable exception) {
        return new UserError(exception.getLocalizedMessage());
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

    public static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            intoUserError(e).print(System.err, "Exception in thread " + t.getName() + ": " + e);
        }
    }
}
