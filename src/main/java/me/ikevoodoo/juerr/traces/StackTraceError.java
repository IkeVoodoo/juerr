package me.ikevoodoo.juerr.traces;

import me.ikevoodoo.juerr.UserError;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class StackTraceError {
    private static final HashMap<Class<? extends Throwable>, BiConsumer<Throwable, UserError>> ERROR_HANDLERS = new HashMap<>();

    static {
        ERROR_HANDLERS.put(NullPointerException.class, (ex, error) -> {
            List<StackTraceLine> lines = StackTraceHelper.getInvolved(ex);
            StackTraceCause cause = StackTraceHelper.getCause(lines.get(0).clazz().get(), lines);

            error.addHelp(cause.generateExplanation(StackTraceMode.TRIED_TO_CALL, ex));
            error.addHelp(cause.generateHelp(StackTraceMode.TRIED_TO_CALL, ex));
            error.addHelp(cause.generateHelp(StackTraceMode.CHECK, ex));

            /*error.addHelp("Surround with a try-catch");
            error.addHelp(UserErrorHelper.snippet(10,
                    "try {",
                    "    Files.readAllLines(null);",
                    "} catch (NullPointerException exception) {",
                    "    // Handle the exception",
                    "    return;",
                    "}"));*/
        });

        ERROR_HANDLERS.put(RuntimeException.class, (ex, error) -> {
            List<StackTraceLine> lines = StackTraceHelper.getInvolved(ex);
            StackTraceCause cause = StackTraceHelper.getCause(lines.get(0).clazz().get(), lines);

            //error.addHelp(cause.generateExplanation(StackTraceMode.TRY_CATCH, ex));
            error.addHelp(cause.generateHelp(StackTraceMode.TRY_CATCH, ex));
        });
    }

    private final UserError error;

    public StackTraceError(UserError error) {
        this.error = error;
    }

    public void apply(Throwable throwable) {
        for (StackTraceElement element : throwable.getStackTrace()) {
            String className = element.getClassName();
            if (className.contains("."))
                className = className.substring(className.lastIndexOf('.') + 1);
            className = className.replace("$", ".");
            this.error.addReason(String.format("%s.%s(%s:%s)", className, element.getMethodName(), element.getFileName(), element.getLineNumber()));
        }

        BiConsumer<Throwable, UserError> consumer = ERROR_HANDLERS.get(throwable.getClass());
        if (consumer != null) {
            consumer.accept(throwable, error);
        }
    }
/*
    private void applyNPE(Throwable throwable) {
        Matcher matcher = NPE_EXTRACTOR.matcher(String.valueOf(throwable.getLocalizedMessage()));
        if (matcher.matches()) {
            String attempted = matcher.group(1);
            String clazz = attempted;
            Matcher classMatcher = CLASS_EXTRACTOR.matcher(attempted);
            if (classMatcher.find()) {
                clazz = classMatcher.group();
                Matcher clazzNameMatcher = CLASS_NAME_EXTRACTOR.matcher(clazz);
                if (clazzNameMatcher.find()) {
                    clazz = clazzNameMatcher.group();
                }
            }
            String var = matcher.group(2);

            this.error.addHelp(String.format("Try checking if %s is not null with an if statement", var));
            this.error.addHelp(String.format("Try wrapping %s in a Optional<%s>", var, clazz));
            this.error.addHelp(String.format("Check where %s is assigned", var));
        }
    }*/
}