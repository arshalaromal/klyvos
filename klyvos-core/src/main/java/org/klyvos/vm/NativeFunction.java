package org.klyvos.vm;

import java.io.IOException;
import java.util.List;

@FunctionalInterface
public interface NativeFunction {
    Value invoke(List<Value> args) throws IOException;
}