package me.ikevoodoo.juerr.traces;

import me.ikevoodoo.juerr.ProjectInfo;
import sun.misc.SharedSecrets;
import sun.reflect.Reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StackTraceHelper {

    private StackTraceHelper() {

    }

    public static List<StackTraceLine> getInvolved(Throwable throwable) {
        List<StackTraceLine> lines = Arrays.stream(throwable.getStackTrace())
                .map(element -> new StackTraceLine(
                        getClass(element.getClassName()),
                        getDeclaredMethod(element.getClassName(), element.getMethodName()),
                        element.getLineNumber(),
                        element.getFileName()))
                .collect(Collectors.toList());
        Collections.reverse(lines);
        return lines;
    }

    public static StackTraceCause getCause(Class<?> clazz, List<StackTraceLine> stackTraceLines) {
        if (!ProjectInfo.isLoaded(clazz)) return null;


        List<Class<?>> classes = ProjectInfo.getClasses(clazz);
        List<StackTraceLine> last = new ArrayList<>();

        for (StackTraceLine line : stackTraceLines) {
            Class<?> cl = line.clazz().get();
            if (classes.contains(cl)) {
                last.add(line);
            }
        }

        if (last.size() > 0) {
            int index = stackTraceLines.indexOf(last.get(last.size() - 1));

            return new StackTraceCause(last, stackTraceLines.get(Math.min(stackTraceLines.size() - 2, index) + 1));
        }

        return null;
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
