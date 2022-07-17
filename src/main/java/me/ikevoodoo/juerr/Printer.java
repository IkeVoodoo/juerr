package me.ikevoodoo.juerr;

public abstract class Printer<T> {

    private final T out;

    protected Printer(T out) {
        this.out = out;
    }

    protected final T getOut() {
        return this.out;
    }

    abstract public void printf(String message, Object... args);

    public void printfln(String message, Object... args) {
        printf(message + '\n', args);
    }
}