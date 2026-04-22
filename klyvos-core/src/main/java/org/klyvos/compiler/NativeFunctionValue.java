package org.klyvos.compiler;

import org.klyvos.vm.NativeFunction;
import org.klyvos.vm.Value;

import java.io.IOException;
import java.util.List;

public class NativeFunctionValue {
    private final String name;
    private final NativeFunction function;

    public NativeFunctionValue(String name, NativeFunction function) {
        this.name = name;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public Value call(List<Value> args) throws IOException {
        return function.invoke(args);
    }

    public String toString() {
        return "<native fn " + name + ">";
    }
}
