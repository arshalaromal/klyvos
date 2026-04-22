package org.klyvos.error;

/**
 * @author Arshal Aromal
 */
public class KlyvosCompileError implements KlyvosError {
    private final String type;
    private final String message;
    private final int line;

    public KlyvosCompileError(String type, String message, Integer line) {
        this.type = type;
        this.message = message;
        this.line = line;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public String format() {
        return "[CompileError] " + type + " at line " + line + ": " + message;
    }

}
