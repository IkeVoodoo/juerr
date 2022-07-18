package me.ikevoodoo.juerr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserErrorEntry {

    private final List<String> lines;

    public UserErrorEntry(List<String> lines) {
        this.lines = lines;
    }

    public static UserErrorEntry from(String... message) {
        List<String> lines = new ArrayList<>();
        Collections.addAll(lines, message);
        return new UserErrorEntry(lines);
    }

    public UserErrorEntry append(UserErrorEntry entry) {
        this.lines.addAll(entry.lines());
        return this;
    }

    public List<String> lines() {
        return lines;
    }
}
