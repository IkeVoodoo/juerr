package me.ikevoodoo.juerr.traces;

import java.lang.reflect.Method;
import java.util.Optional;

public class StackTraceLine {



    private final Class<?> clazz;
    private final Method method;
    private final int line;
    private final String file;

    public StackTraceLine(Class<?> clazz, Method method, int line, String file) {
        this.clazz = clazz;
        this.method = method;
        this.line = line;
        this.file = file;
    }

    public Optional<Class<?>> clazz() {
        return Optional.ofNullable(clazz);
    }

    public Optional<Method> method() {
        return Optional.ofNullable(method);
    }

    public int line() {
        return line;
    }

    public String file() {
        return file;
    }

    @Override
    public String toString() {
        return String.format("StackTraceLine[clazz=%s, method=%s, line=%s, file=%s]", clazz, method, line, file);
    }
}
