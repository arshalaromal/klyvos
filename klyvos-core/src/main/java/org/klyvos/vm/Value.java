package org.klyvos.vm;

import org.klyvos.compiler.NativeFunctionValue;
import org.klyvos.error.KlyvosRuntimeError;

import java.util.List;
import java.util.Objects;

public class Value {
    public enum Type {INT, DECIMAL, BOOL, STRING, LIST, NATIVE_FN, USER_FN, MODULE, ERROR, NULL}

    public final Type type;
    public final Object value;

    public static final Value NULL = new Value(Type.NULL, null);

    private Value(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static Value ofInt(long v) {
        return new Value(Type.INT, v);
    }

    public static Value ofDecimal(double v) {
        return new Value(Type.DECIMAL, v);
    }

    public static Value ofBool(boolean v) {
        return new Value(Type.BOOL, v);
    }

    public static Value ofString(String v) {
        return new Value(Type.STRING, v);
    }

    public static Value ofList(List<Value> v) {
        return new Value(Type.LIST, v);
    }

    public static Value ofNativeFunction(NativeFunction f) {
        return new Value(Type.NATIVE_FN, f);
    }

    public static Value ofUserFunction(UserFunction f) {
        return new Value(Type.USER_FN, f);
    }

    public static Value ofFunction(Object f) {
        if (f instanceof UserFunction userFn) {
            return new Value(Type.USER_FN, userFn);
        } else if (f instanceof NativeFunctionValue nativeFn) {
            return new Value(Type.NATIVE_FN, nativeFn);
        } else {
            throw new IllegalArgumentException("Unsupported function type: " + f.getClass());
        }
    }

    public static Value of(ModuleNamespace ns) {
        return new Value(Type.MODULE, ns);
    }

    public static Value ofError(KlyvosRuntimeError err) {
        return new Value(Type.ERROR, err);
    }

    public static Value ofError(Exception e) {
        return new Value(Type.ERROR, e);
    }

    // General "of" for literals, strings, lists, numbers, null
    @SuppressWarnings("unchecked")
    public static Value of(Object o) {
        if (o == null) return NULL;
        if (o instanceof Value v) return v;
        if (o instanceof Long l) return ofInt(l);
        if (o instanceof Integer i) return ofInt(i);
        if (o instanceof Double d) return ofDecimal(d);
        if (o instanceof Boolean b) return ofBool(b);
        if (o instanceof String s) return ofString(s);
        if (o instanceof List<?> l) return ofList((List<Value>) l);
        if (o instanceof UserFunction f) return ofUserFunction(f);
        if (o instanceof NativeFunction nf) return ofNativeFunction(nf);
        if (o instanceof ModuleNamespace ns) return of(ns);
        if (o instanceof KlyvosRuntimeError err) return ofError(err);
        throw new IllegalArgumentException("Unsupported value: " + o);
    }

    public boolean isInt() {
        return type == Type.INT;
    }

    public boolean isDecimal() {
        return type == Type.DECIMAL;
    }

    public boolean isBool() {
        return type == Type.BOOL;
    }

    public boolean isString() {
        return type == Type.STRING;
    }

    public boolean isList() {
        return type == Type.LIST;
    }

    public boolean isNativeFn() {
        return type == Type.NATIVE_FN;
    }

    public boolean isUserFn() {
        return type == Type.USER_FN;
    }

    public boolean isModule() {
        return type == Type.MODULE;
    }

    public boolean isNull() {
        return type == Type.NULL;
    }

    public boolean isError() {
        return type == Type.ERROR;
    }

    public long asInt() {
        return (Long) value;
    }

    public double asDecimal() {
        return (Double) value;
    }

    public boolean asBool() {
        return (Boolean) value;
    }

    public String asString() {
        return (String) value;
    }

    @SuppressWarnings("unchecked")
    public List<Value> asList() {
        return (List<Value>) value;
    }

    public NativeFunction asNativeFn() {
        return (NativeFunction) value;
    }

    public UserFunction asUserFn() {
        return (UserFunction) value;
    }

    public ModuleNamespace asModule() {
        return (ModuleNamespace) value;
    }

    public Exception asError() {
        return (Exception) value;
    }

    @Override
    public String toString() {
        return switch (type) {
            case NULL -> "null";
            case INT -> Long.toString((Long) value);
            case DECIMAL -> Double.toString((Double) value);
            case BOOL -> Boolean.toString((Boolean) value);
            case STRING -> (String) value;
            case LIST -> value.toString();
            case NATIVE_FN -> "<native-fn>";
            case USER_FN -> "<user-fn>";
            case MODULE -> "<module>";
            case ERROR -> "<error: " + value + ">";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Value)) return false;
        Value v = (Value) o;
        if (type != v.type) return false;
        return Objects.equals(value, v.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}