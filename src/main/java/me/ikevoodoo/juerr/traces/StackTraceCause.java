package me.ikevoodoo.juerr.traces;

import me.ikevoodoo.juerr.ArrayUtils;
import me.ikevoodoo.juerr.UserErrorEntry;
import me.ikevoodoo.juerr.UserErrorHelper;

import java.util.List;

public class StackTraceCause {

    private final List<StackTraceLine> last;
    private final StackTraceLine error;

    public StackTraceCause(List<StackTraceLine> last, StackTraceLine error) {
        this.last = last;
        this.error = error;
    }

    public List<StackTraceLine> last() {
        return last;
    }

    public StackTraceLine error() {
        return error;
    }

    public UserErrorEntry generateExplanation(StackTraceMode mode, Throwable throwable) {
        switch (mode) {
            case TRIED_TO_CALL:
                String template = "%s.%s(%s) tried to call %s.%s(%s) on line %s in file %s";
                StackTraceLine bef = last.get(last.size() - 1);
                return UserErrorEntry.from(String.format(template,
                        bef.clazz().get().getSimpleName(),
                        bef.method().get().getName(),
                        ArrayUtils.toString(bef.method().get().getParameterTypes()),
                        error.clazz().get().getSimpleName(),
                        error.method().get().getName(),
                        getErrorCalled(throwable),
                        bef.line(),
                        bef.file()));
            default:
                return UserErrorEntry.from();
        }
    }

    public UserErrorEntry generateHelp(StackTraceMode mode, Throwable throwable) {
        switch (mode) {
            case TRIED_TO_CALL:
                if (throwable instanceof NullPointerException) {
                    UserErrorEntry entry = UserErrorEntry.from(String.format(
                            "Try passing in a %s instance to %s.%s(%s)",
                            ArrayUtils.toString(error.method().get().getParameterTypes()),
                            error.clazz().get().getSimpleName(),
                            error.method().get().getName(),
                            ArrayUtils.toString(error.method().get().getParameterTypes())
                    ));
                    StackTraceLine bef = last.get(last.size() - 1);
                    entry.append(UserErrorHelper.snippet(bef.line(),
                            String.format(
                                    "%s.%s(%s);",
                                    error.clazz().get().getSimpleName(),
                                    error.method().get().getName(),
                                    getErrorParamExample()
                            )
                    ));
                    return entry;
                }
            case TRY_CATCH:
                UserErrorEntry entry = UserErrorEntry.from("Wrap in a try-catch");
                StackTraceLine bef = last.get(last.size() - 1);
                entry.append(UserErrorHelper.snippet(bef.line(),
                                "try {",
                                String.format("  %s.%s(%s);",
                                        error.clazz().get().getSimpleName(),
                                        error.method().get().getName(),
                                        ArrayUtils.toString(error.method().get().getParameterTypes())
                                ),
                                String.format("} catch (%s exception) {", throwable.getClass().getSimpleName()),
                                "  // Handle your error",
                                "  return;",
                                "}"
                        )
                );
                return entry;
            case CHECK:
                bef = last.get(last.size() - 1);
                String assign = "%s %s = %s;";
                String condition = "%s %s= %s";
                String ifCheck = "if (%s) {";
                String body = "  %s.%s(%s);";
                String end = "}";
                Class<?>[] params = error.method().get().getParameterTypes();
                String[] arr = new String[3 + params.length];
                String[] conditions = new String[params.length];
                String[] vars = new String[params.length];
                for (int i = 0; i < params.length; i++) {
                    arr[i] = formatDeclaration(params[i], i, assign);
                    conditions[i] = formatCondition(params[i], condition, i, "!", getCheckAgainst(throwable));
                    vars[i] = getVar(params[i], i);
                }
                arr[arr.length - 3] = String.format(ifCheck, String.join("&&", conditions));
                arr[arr.length - 2] = String.format(body, error.clazz().get().getSimpleName(),
                        error.method().get().getName(), String.join(",", vars));
                arr[arr.length - 1] = end;
                UserErrorEntry snippet =  UserErrorHelper.snippet(bef.line(),
                        arr);
                return UserErrorEntry.from("Check the value").append(snippet);
            default:
                return UserErrorEntry.from();
        }
    }

    private String getCheckAgainst(Throwable throwable) {
        if (throwable instanceof NullPointerException) {
            return null;
        }

        return "";
    }

    private String formatDeclaration(Class<?> clazz, int i, String template) {
        String name = getVar(clazz, i);
        return String.format(template, clazz.getSimpleName(),
                name,
                generateExample(clazz));
    }

    private String formatCondition(Class<?> clazz, String template, int i, String cond, String checkAgainst) {
        return String.format(template, getVar(clazz, i), cond, checkAgainst);
    }

    private String getVar(Class<?> clazz, int i) {
        String name = clazz.getSimpleName();
        return Character.toLowerCase(name.charAt(0)) + name.substring(1) + i;
    }

    private String getErrorParamExample() {
        Class<?>[] types = error.method().get().getParameterTypes();
        if (types.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (Class<?> type : types) {
            sb.append(generateExample(type)).append(", ");
        }
        while (sb.charAt(sb.length() - 2) == ',')
            sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    private String generateExample(Class<?> clazz) {
        return StackTraceGenerator.generate(clazz);
    }

    private String getErrorCalled(Throwable throwable) {
        if (throwable instanceof NullPointerException) {
            return null;
        }

        return ArrayUtils.toString(error.method().get().getParameterTypes());
    }

    @Override
    public String toString() {
        return String.format("StackTraceCause[last=%s, error=%s]", last, error);
    }
}
