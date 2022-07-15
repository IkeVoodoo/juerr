package me.ikevoodoo;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UserError {

    private final String message;
    private final List<String> help;
    private final List<String> reasons;

    public UserError(String message) {
        this.message = message;
        this.help = new ArrayList<>();
        this.reasons = new ArrayList<>();
    }

    public static UserError from(String message) {
        return new UserError(message);
    }

    public static UserError intoUserError(Error error) {
        return new UserError(error.getLocalizedMessage());
    }

    public static UserError intoUserError(Exception exception) {
        return new UserError(exception.getLocalizedMessage());
    }

    public UserError printAll(String prefix) {
        return this.printAll(System.err, prefix);
    }

    public UserError printAll(PrintStream stream, String prefix) {
        this.print(stream, prefix);
        return this;
    }

    public UserError addHelp(String help) {
        this.help.add(help);
        return this;
    }

    public UserError addReason(String reason) {
        this.reasons.add(reason);
        return this;
    }

    public String message() {
        return this.message;
    }

    public List<String> reasons() {
        return this.reasons;
    }

    public List<String> help() {
        return this.help;
    }

    private void print(PrintStream stream, String prefix) {
        stream.printf("%s%s\n", prefix, this.message);
        this.printList(stream, this.reasons, " - caused by: ", "     |        ");
        this.printList(stream, this.help, " + help: ", "     |   ");
    }

    private void printList(PrintStream stream, List<String> list, String prefix, String joiner) {
        if (list.isEmpty()) return;
        stream.printf("%s%s\n", prefix, list.get(0));
        list.subList(1, list.size()).forEach(entry -> stream.printf("%s%s\n", joiner, entry));
    }

}
