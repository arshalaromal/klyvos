package org.klyvos.vm;

import org.klyvos.error.KlyvosRuntimeError;

import java.util.Map;

public class ModuleNamespace {
    private final String moduleName;
    private final Map<String, Value> exports;

    public ModuleNamespace(String moduleName, Map<String, Value> exports) {
        this.moduleName = moduleName;
        this.exports = exports;


    }

    public Value get(String name, int line) {
        if (!exports.containsKey(name)) {
            return Value.ofError(new KlyvosRuntimeError(
                    "AttributeError",
                    "Module '" + moduleName + "' has no export '" + name + "'",
                    line));
        }
        return exports.get(name);
    }
}