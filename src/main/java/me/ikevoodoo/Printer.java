package me.ikevoodoo;

public abstract class Printer<T> {

    private final T out;

    protected Printer(T out) {
        this.out = out;
    }

    protected T getOut() {
        return this.out;
    }

    abstract void printf(String message, Object... args);
}