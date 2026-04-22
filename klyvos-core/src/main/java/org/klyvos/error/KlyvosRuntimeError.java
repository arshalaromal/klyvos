package org.klyvos.error;

public class KlyvosRuntimeError extends RuntimeException {
    public final String type;
    public final String message;
    public final int line;

    public KlyvosRuntimeError(String type, String message, int line) {
        super(message);
        this.type = type;
        this.message = message;
        this.line = line;
    }

    @Override
    public String toString() {
        return type + ": " + message + " @ line " + line;
    }
}