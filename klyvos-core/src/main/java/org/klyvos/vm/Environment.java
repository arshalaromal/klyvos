package org.klyvos.vm;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    public Map<String, Value> getVariables() {
        return variables;
    }

    final Map<String, Value> variables = new HashMap<>();

    public void define(String name, Value value) {
        variables.put(name, value);
    }

    public Value get(String name, int line) {
        return variables.getOrDefault(name, Value.NULL);
    }

    public boolean isDefined(String name) {
        return variables.containsKey(name);
    }

    public void merge(Environment other) {
        this.variables.putAll(other.variables);

    }
}