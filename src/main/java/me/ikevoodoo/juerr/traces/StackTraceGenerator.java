package me.ikevoodoo.juerr.traces;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.Function;

public class StackTraceGenerator {

    private static final HashMap<Class<?>, Function<Class<?>, String>> GENERATORS = new HashMap<>();

    static {
        GENERATORS.put(Path.class, clazz -> "Path.of(...)");
        GENERATORS.put(Class.class, clazz -> "Class.forName(...)");
    }

    public static String generate(Class<?> clazz) {
        Function<Class<?>, String> func = GENERATORS.get(clazz);
        if (func == null) return "";

        String msg = func.apply(clazz);
        return msg == null ? "" : msg;
    }
}
