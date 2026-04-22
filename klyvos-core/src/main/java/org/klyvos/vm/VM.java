package org.klyvos.vm;

import org.klyvos.compiler.OpCode;
import org.klyvos.error.KlyvosRuntimeError;
import org.klyvos.modules.ModuleRegistry;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class VM {


    private static final int STACK_MAX = 1024;

    private final Deque<Value> stack = new ArrayDeque<>();
    private final Deque<CallFrame> callFrames = new ArrayDeque<>();
    public int instructionCount = 0;
    private final Environment globals = new Environment();
    private final ModuleRegistry moduleRegistry;
    private final Map<String, Map<String, NativeFunction>> nativeModules = new HashMap<>();
    private int line = -1;

    public VM(ModuleRegistry registry) {
        this.moduleRegistry = registry;
    }

    public Environment getGlobals() {
        return globals;
    }

    public void registerNative(String module, String name, NativeFunction fn) {
        nativeModules.computeIfAbsent(module, k -> new HashMap<>()).put(name, fn);
    }

    private void push(Value v) {
        if (stack.size() >= STACK_MAX)
            throw new KlyvosRuntimeError("StackOverflow", "Stack limit exceeded", -1);
        stack.push(v);
    }

    private Value pop() {
        if (stack.isEmpty())
            throw new KlyvosRuntimeError("StackUnderflow", "Cannot pop from empty stack", -1);
        return stack.pop();
    }

    private Value peek(int offset) {
        Iterator<Value> it = stack.iterator();
        for (int i = 0; i < offset && it.hasNext(); i++) it.next();
        return it.hasNext() ? it.next() : Value.NULL;
    }

    public Value runTopLevel(Chunk chunk) {
        callFrames.clear();
        stack.clear();
        ChunkReader r = new ChunkReader(chunk);
        callFrames.push(new CallFrame(null, r, 0, 0));
        try {
            return run();
        } catch (Exception e) {
            return Value.ofError(e);

        }
    }

    private Value run() throws Exception {
        while (!callFrames.isEmpty()) {
            CallFrame frame = callFrames.peek();
            ChunkReader r = frame.reader;
            Chunk chunk = r.getChunk();

            if (!r.hasNext()) {
                callFrames.pop();
                continue;
            }

            int offset = r.getIP();
            byte raw = r.readByte();
            OpCode op = OpCode.fromByte(raw);
            line = chunk.getLine(offset);
            frame.currentLine = line;
            instructionCount++;

            switch (op) {
                // all your previous opcode implementations...


                case LOAD_CONST -> push(chunk.getConstant(r.readShort()));

                case LOAD_VAR -> {
                    int slot = r.readShort();
                    push(frame.getLocal(slot));
                }


                case STORE_VAR -> {
                    int slot = r.readShort();
                    frame.setLocal(slot, pop());
                }

                case LOAD_GLOBAL -> {
                    String name = chunk.getConstant(r.readShort()).asString();
                    push(globals.get(name, line));
                }

                case STORE_GLOBAL -> {
                    String name = chunk.getConstant(r.readShort()).asString();
                    globals.define(name, peek(0));
                }

                case POP -> {
                    stack.pop();
                }

                case BREAK -> {
                    int target = r.readShort();
                    r.setIP(target);
                }

                case CONTINUE -> {
                    int target = r.readShort();
                    r.setIP(target);
                }

                case MAKE_LIST -> {
                    int count = Byte.toUnsignedInt(r.readByte());
                    List<Value> list = new ArrayList<>(count);
                    for (int i = 0; i < count; i++) list.add(0, pop()); // reverse order
                    push(Value.ofList(list));
                }

                case GET_INDEX -> {
                    Value indexVal = pop();
                    Value container = pop();

                    if (!indexVal.isInt())
                        return runtimeError("Index must be integer", line);

                    long index = indexVal.asInt();
                    if (index < 0)
                        return runtimeError("Negative indexing is not allowed", line);

                    if (container.isList()) {
                        List<Value> list = container.asList();
                        if (index >= list.size())
                            return runtimeError("List index out of bounds", line);
                        push(list.get((int) index));
                    } else if (container.isString()) {
                        String str = container.asString();
                        if (index >= str.length())
                            return runtimeError("String index out of bounds", line);
                        push(Value.ofString(String.valueOf(str.charAt((int) index))));
                    } else {
                        return runtimeError("Cannot index into type " + container.type, line);
                    }
                }


                case SLICE -> {
                    Value stepVal = pop();
                    Value endVal = pop();
                    Value startVal = pop();
                    Value container = pop();

                    if (!container.isList() && !container.isString())
                        return runtimeError("Can only slice lists or strings", line);

                    long start = startVal.isNull() ? 0 : startVal.asInt();
                    long end = endVal.isNull()
                            ? (container.isList() ? container.asList().size() : container.asString().length())
                            : endVal.asInt();
                    long step = stepVal.isNull() ? 1 : stepVal.asInt();

                    if (start < 0 || end < 0 || step <= 0)
                        return runtimeError("Slice indices and step must be non-negative and step > 0", line);

                    if (container.isList()) {
                        List<Value> list = container.asList();
                        List<Value> sliced = new ArrayList<>();

                        for (long i = start; i < end && i < list.size(); i += step)
                            sliced.add(list.get((int) i));

                        push(Value.ofList(sliced));

                    } else { // string slice
                        String str = container.asString();
                        StringBuilder result = new StringBuilder();

                        for (long i = start; i < end && i < str.length(); i += step)
                            result.append(str.charAt((int) i));

                        push(Value.ofString(result.toString()));
                    }
                }


                case ADD -> {
                    Value b = pop(), a = pop();
                    if (a.isInt() && b.isInt()) push(Value.ofInt(a.asInt() + b.asInt()));
                    else if (a.isDecimal() && b.isDecimal()) push(Value.ofDecimal(a.asDecimal() + b.asDecimal()));
                    else if (a.isString() || b.isString()) push(Value.ofString(a.toString() + b.toString()));
                    else if (a.isList() && b.isList()) {
                        push(Value.ofList(Stream.concat(a.asList().stream(), b.asList().stream()).toList()));

                    } else return runtimeError("Invalid types for +", line);
                }
                case SUB -> {
                    Value b = pop(), a = pop();
                    if (a.isInt() && b.isInt()) push(Value.ofInt(a.asInt() - b.asInt()));
                    else if (a.isDecimal() && b.isDecimal()) push(Value.ofDecimal(a.asDecimal() - b.asDecimal()));
                    else return runtimeError("Invalid types for +", line);
                }
                case MOD -> {
                    Value b = pop(), a = pop();
                    if (a.isInt() && b.isInt()) push(Value.ofInt(a.asInt() % b.asInt()));
                    else if (a.isDecimal() && b.isDecimal()) push(Value.ofDecimal(a.asDecimal() % b.asDecimal()));
                    else return runtimeError("Invalid types for +", line);
                }

                case DIV -> {
                    Value b = pop(), a = pop();
                    if (a.isInt() && b.isInt()) push(Value.ofInt(a.asInt() / b.asInt()));
                    else if (a.isDecimal() && b.isDecimal()) push(Value.ofDecimal(a.asDecimal() / b.asDecimal()));
                    else return runtimeError("Invalid types for +", line);
                }

                case EXP -> {
                    Value b = pop(), a = pop();
                    if (a.isInt() && b.isInt())
                        push(Value.ofInt((long) Math.pow((double) a.asInt(), (double) b.asInt())));
                    else if (a.isDecimal() && b.isDecimal())
                        push(Value.ofDecimal((long) Math.pow(a.asDecimal(), b.asDecimal())));
                    else return runtimeError("Invalid types for +", line);
                }

                case MUL -> {
                    Value b = pop(), a = pop();
                    if (a.isInt() && b.isInt()) push(Value.ofInt(a.asInt() * b.asInt()));
                    else if (a.isDecimal() && b.isDecimal()) push(Value.ofDecimal(a.asDecimal() * b.asDecimal()));
                    else return runtimeError("Invalid types for +", line);
                }

                case NEG -> {
                    Value v = pop();
                    if (v.isDecimal()) {
                        push(Value.ofDecimal(-v.asDecimal()));
                    } else if (v.isInt()) {
                        push(Value.ofInt(-v.asInt()));
                    }
                }


                case GET_PROP -> {
                    Value obj = pop();
                    String prop = chunk.getConstant(r.readShort()).asString();
                    if (obj.isModule()) push(obj.asModule().get(prop, line));
                    else if (obj.isString() && prop.equals("length"))
                        push(Value.ofInt(obj.asString().length()));
                    else if (obj.isList()) {
                        if (prop.equals("length")) push(Value.ofInt(obj.asList().size()));
                        else if (List.of("get", "set", "append", "pop").contains(prop))

                            push(Value.ofNativeFunction(builtinMethods(prop)));
                        else return runtimeError("Unknown property: " + prop, line);
                    } else return runtimeError("Cannot GET_PROP on " + obj.type, line);
                }


                case HALT -> {
                    callFrames.pop();
                    if (!stack.isEmpty()) return pop();
                    return Value.NULL;
                }

                case JUMP_IF_FALSE -> {
                    Value condition = pop();  // ✅ correctly pops
                    boolean shouldJump = condition.isNull() || (condition.isBool() && !condition.asBool());
                    int jumpOffset = r.readShort();
                    if (shouldJump) r.setIP(jumpOffset);
                }

                case JUMP -> {
                    int jumpOffset = r.readShort();
                    r.setIP(jumpOffset);
                }


                case NATIVE_CALL -> {
                    String mod = chunk.getConstant(r.readShort()).asString();
                    String fn = chunk.getConstant(r.readShort()).asString();
                    int argc = r.readShort();
                    NativeFunction nativeFn = nativeModules.getOrDefault(mod, Map.of()).get(fn);
                    if (nativeFn == null) return runtimeError("Native fn missing", line);
                    List<Value> args = new ArrayList<>();
                    for (int i = 0; i < argc; i++) args.add(0, pop());
                    Value ret = nativeFn.invoke(args);
                    push(ret != null ? ret : Value.NULL);
                }


                case RETURN -> {
                    Value ret = pop();
                    callFrames.pop();
                    if (callFrames.isEmpty()) return ret;
                    push(ret);
                }

                case EQ -> {
                    Value b = pop();
                    Value a = pop();
                    push(Value.ofBool(a.equals(b)));
                }


                case NEQ -> {
                    Value b = pop();
                    Value a = pop();
                    push(Value.ofBool(!a.equals(b)));
                }

                case IN -> {
                    Value container = pop(); // iterable
                    Value indexVal = pop();  // index

                    if (container.isList()) {
                        List<Value> list = container.asList();

                        if (!indexVal.isInt()) {
                            return runtimeError("'in' index must be integer for lists", line);
                        }

                        long index = indexVal.asInt();
                        boolean ok = index >= 0 && index < list.size();
                        push(Value.ofBool(ok));
                    } else if (container.isString()) {
                        if (!indexVal.isString()) {
                            return runtimeError("'in' index must be string for strings", line);
                        }

                        push(Value.ofBool(container.asString().contains(indexVal.asString())));
                    } else {
                        return runtimeError("Unsupported types for 'in' operator: " + indexVal.type + " in " + container.type, line);
                    }
                }


                case SWAP -> {
                    Value a = pop();
                    Value b = pop();
                    push(a);
                    push(b);
                }

                case NOOP -> {
                    // do nothing
                }

                case DUP -> {
                    Value top = peek(0);
                    push(top);
                }


                case LT -> {
                    Value b = pop();
                    Value a = pop();
                    if (a.isInt() && b.isInt()) {
                        push(Value.ofBool(a.asInt() < b.asInt()));
                    } else if (a.isDecimal() && b.isDecimal()) {
                        push(Value.ofBool(a.asDecimal() < b.asDecimal()));
                    } else {
                        return runtimeError("Cannot compare < for types " + a.type + " and " + b.type, line);
                    }
                }

                case GT -> {
                    Value b = pop();
                    Value a = pop();
                    if (a.isInt() && b.isInt()) {
                        push(Value.ofBool(a.asInt() > b.asInt()));
                    } else if (a.isDecimal() && b.isDecimal()) {
                        push(Value.ofBool(a.asDecimal() > b.asDecimal()));
                    } else {
                        return runtimeError("Cannot compare > for types " + a.type + " and " + b.type, line);
                    }
                }

                case LTE -> {
                    Value b = pop();
                    Value a = pop();
                    if (a.isInt() && b.isInt()) {
                        push(Value.ofBool(a.asInt() <= b.asInt()));
                    } else if (a.isDecimal() && b.isDecimal()) {
                        push(Value.ofBool(a.asDecimal() <= b.asDecimal()));
                    } else {
                        return runtimeError("Cannot compare <= for types " + a.type + " and " + b.type, line);
                    }
                }

                case GTE -> {
                    Value b = pop();
                    Value a = pop();
                    if (a.isInt() && b.isInt()) {
                        push(Value.ofBool(a.asInt() >= b.asInt()));
                    } else if (a.isDecimal() && b.isDecimal()) {
                        push(Value.ofBool(a.asDecimal() >= b.asDecimal()));
                    } else {
                        return runtimeError("Cannot compare >= for types " + a.type + " and " + b.type, line);
                    }
                }

                case NOT -> {
                    Value v = pop();
                    if (v.isBool()) {
                        push(Value.ofBool(!v.asBool()));
                    } else if (v.isNull()) {
                        push(Value.ofBool(true)); // null is falsy
                    } else {
                        push(Value.ofBool(false)); // anything else is truthy
                    }
                }


                case USE_MODULE -> {
                    String modName = chunk.getConstant(r.readShort()).asString();
                    // try script import
                    try {
                        Chunk modChunk = moduleRegistry.loadScriptModule(modName, line);
                        VM sub = new VM(moduleRegistry);
                        sub.nativeModules.putAll(this.nativeModules);
                        Value result = sub.runTopLevel(modChunk);
                        if (result.isError()) return result;
                        Map<String, Value> exports = new HashMap<>(sub.getGlobals().getVariables());
                        push(Value.of(new ModuleNamespace(modName, exports)));
                        break;
                    } catch (IOException | KlyvosRuntimeError ignored) {
                        // either not found on disk or NameError from loadScriptModule:
                        // fall back to native
                    }

                    Map<String, NativeFunction> natives = nativeModules.get(modName);
                    if (natives == null) {
                        return runtimeError("Module not found: " + modName, line);
                    }
                    Map<String, Value> exports = new HashMap<>();
                    for (var e : natives.entrySet()) {
                        exports.put(e.getKey(), Value.ofNativeFunction(e.getValue()));
                    }
                    push(Value.of(new ModuleNamespace(modName, exports)));
                }

                case CALL -> {
                    int argc = r.readShort();
                    Value callee = pop();
                    if (!callee.isUserFn()) return runtimeError("Not a function", line);
                    UserFunction fn = callee.asUserFn();
                    int total = fn.getParamNames().size();
                    if (argc > total) return runtimeError("Too many args", line);

                    Value[] locals = new Value[total];
                    for (int i = argc - 1; i >= 0; i--) locals[i] = pop();
                    for (int i = argc; i < total; i++) {
                        Chunk dc = fn.getDefaultChunks().get(i);
                        if (dc == null) return runtimeError("Missing arg " + fn.getParamNames().get(i), line);
                        VM temp = new VM(moduleRegistry);
                        temp.nativeModules.putAll(this.nativeModules);
                        Value dv = temp.runTopLevel(dc);
                        if (dv.isError()) return dv;
                        locals[i] = dv;
                    }

                    ChunkReader cr = new ChunkReader(fn.getChunk());
                    CallFrame nf = new CallFrame(fn, cr, stack.size(), total);
                    nf.locals = locals;
                    callFrames.push(nf);
                }

                case CALL_METHOD -> {
                    String name = chunk.getConstant(r.readShort()).asString();
                    int argc = r.readShort();
                    List<Value> args = new ArrayList<>();
                    for (int i = 0; i < argc; i++) args.add(0, pop());
                    Value receiver = pop();

                    if (receiver.isModule()) {
                        Value method = receiver.asModule().get(name, line);
                        if (method == null || method.isNull()) {
                            return runtimeError("Module has no method named '" + name + "'", line);
                        }

                        if (method.isNativeFn()) {
                            NativeFunction nf = method.asNativeFn();
                            try {
                                push(nf.invoke(args));
                            } catch (Exception e) {
                                return runtimeError("Native method error: " + e.getMessage(), line);
                            }
                        } else if (method.isUserFn()) {
                            UserFunction fn = method.asUserFn();
                            int total = fn.getParamNames().size();
                            if (args.size() > total)
                                return runtimeError("Too many arguments for method: expected " + total, line);

                            Value[] locals = new Value[total];
                            for (int i = 0; i < args.size(); i++) locals[i] = args.get(i);
                            for (int i = args.size(); i < total; i++) {
                                Chunk defChunk = fn.getDefaultChunks().get(i);
                                if (defChunk != null) {
                                    VM temp = new VM(moduleRegistry);
                                    temp.nativeModules.putAll(this.nativeModules);
                                    Value val = temp.runTopLevel(defChunk);
                                    if (val.isError()) return val;
                                    locals[i] = val;
                                } else {
                                    return runtimeError("Missing method arg '" + fn.getParamNames().get(i) + "'", line);
                                }
                            }

                            ChunkReader methodReader = new ChunkReader(fn.getChunk());
                            CallFrame newFrame = new CallFrame(fn, methodReader, stack.size(), total);
                            newFrame.locals = locals;
                            callFrames.push(newFrame);
                        } else {
                            return runtimeError("Module method is not callable", line);
                        }
                    } else {
                        // handle string or list method
                        NativeFunction builtin = builtinMethods(name);
                        if (builtin == null) return runtimeError("Unknown method: " + name, line);
                        args.add(0, receiver);
                        try {
                            push(builtin.invoke(args));
                        } catch (Exception e) {
                            return runtimeError("Built-in method error: " + e.getMessage(), line);
                        }
                    }
                }

                // other unchanged opcodes...

                default -> {
                    return runtimeError("Unsupported opcode: " + op, line);
                }
            }
        }

        return Value.NULL;
    }

    // keep the rest of your helper methods as-is...

    private NativeFunction builtinMethods(String name) {
        return switch (name) {
            case "get" -> args -> {

                if (args.get(0).isList()) {
                    int i = (int) args.get(1).asInt();
                    return args.get(0).asList().get(i);
                } else if (args.get(0).isString()) {
                    int i = (int) args.get(1).asInt();
                    return Value.ofString(String.valueOf(args.get(0).asString().charAt(i)));
                }
                return runtimeError("Cannot use " + name + " on " + args.get(0).type.name(), line);
            };
            case "set" -> args -> {
                if (args.get(0).isList()) {
                    int i = (int) args.get(1).asInt();
                    args.get(0).asList().set(i, args.get(2));
                    return Value.NULL;
                }
                return runtimeError("Cannot use " + name + " on " + args.get(0).type.name(), line);

            };
            case "length" -> args -> {
                if (args.get(0).isList())
                    return Value.ofInt(args.get(0).asList().size());
                else if (args.get(0).isString())
                    return Value.ofInt(args.get(0).asString().length());
                return runtimeError("Cannot use " + name + " on " + args.get(0).type.name(), line);

            };

            case "append" -> args -> {
                if (args.get(0).isList()) {
                    args.get(0).asList().add(args.get(1));
                    return Value.NULL;
                } else if (args.get(0).isString()) {
                    return Value.ofString(args.get(0).asString() + args.get(1).asString());
                }
                return runtimeError("Cannot use " + name + " on " + args.get(0).type.name(), line);

            };

            case "pop" -> args -> {
                if (args.get(0).isList()) {
                    if (args.size() == 1) {
                        return args.get(0).asList().removeLast();
                    }
                    return args.get(0).asList().remove(Math.toIntExact(args.get(1).asInt()));
                }
                return runtimeError("Cannot use " + name + " on " + args.get(0).type.name(), line);

            };

            case "toInt" -> args -> {
                if (args.get(0).isString()) {
                    try {
                        return Value.ofInt(Long.valueOf(args.get(0).asString()));
                    } catch (Exception e) {
                        return runtimeError("Error while parsing int from string: " + args.get(0),
                                line);
                    }
                }
                return runtimeError("Cannot use " + name + " on " + args.get(0).type.name(), line);
            };

            default -> args -> Value.NULL;
        };
    }

    private Value runtimeError(String msg, int line) {
        throw new KlyvosRuntimeError("RuntimeError", msg, line);
    }

}
