package org.klyvos.error;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ErrorReporter {
    private final PrintWriter writer;
    private final List<KlyvosError> errors = new ArrayList<>();

    public ErrorReporter(OutputStream outputStream) {
        this.writer = new PrintWriter(outputStream, true);
    }

    public void report(KlyvosError error) {
        errors.add(error);
        writer.println(error.format());
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<KlyvosError> getAll() {
        return errors;
    }

    public void flush() {
        writer.flush();
    }
}

