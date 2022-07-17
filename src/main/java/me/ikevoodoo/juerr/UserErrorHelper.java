package me.ikevoodoo.juerr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserErrorHelper {

    private UserErrorHelper() {

    }

    public static UserErrorEntry snippet(String... lines) {
        List<String> snippet = new ArrayList<>();
        snippet.add("");
        snippet.add("--- SNIPPET ---");
        Collections.addAll(snippet, lines);
        snippet.add("--- SNIPPET ---");
        snippet.add("");
        snippet.add("");

        return new UserErrorEntry(snippet);
    }

    public static UserErrorEntry snippet(int startLine, String... lines) {
        String[] numberedLines = new String[lines.length];
        for (int i = 0; i < lines.length; i++) {
            numberedLines[i] = String.format("%s. %s", startLine + i, lines[i]);
        }
        return snippet(numberedLines);
    }

}
