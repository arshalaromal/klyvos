package org.klyvos.error;

/**
 * @author Arshal Aromal
 */
public interface KlyvosError {
    String getType();

    String getMessage();

    int getLine();

    String format();
}
