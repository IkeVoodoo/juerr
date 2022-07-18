package me.ikevoodoo.juerr;

public class ArrayUtils {

    private ArrayUtils() {

    }

    public static String toString(Class<?>... classes) {
        if (classes.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (Class<?> aClass : classes) {
            sb.append(aClass.getSimpleName()).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }
}
