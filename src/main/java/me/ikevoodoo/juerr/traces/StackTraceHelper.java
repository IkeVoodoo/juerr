package me.ikevoodoo.juerr.traces;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StackTraceHelper {

    private StackTraceHelper() {

    }

    public static List<StackTraceLine> getInvolved(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
                .map(element -> new StackTraceLine(
                        getClass(element.getClassName()),
                        getDeclaredMethod(element.getClassName(), element.getMethodName()),
                        element.getLineNumber(),
                        element.getFileName()))
                .collect(Collectors.toList());
    }

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Method getDeclaredMethod(String className, String name) {
        Class<?> clazz = getClass(className);
        if (clazz == null) return null;
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

}
