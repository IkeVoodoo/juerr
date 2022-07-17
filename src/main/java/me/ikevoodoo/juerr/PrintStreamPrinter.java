package me.ikevoodoo.juerr;

import java.io.PrintStream;

public class PrintStreamPrinter extends Printer<PrintStream> {

    protected PrintStreamPrinter(PrintStream out) {
        super(out);
    }

    @Override
    public void printf(String message, Object... args) {
        getOut().printf(message, args);
    }
}