package org.klyvos.vm;

public class CallFrame {
    public final UserFunction function;
    public int ip;
    public final ChunkReader reader;
    public final int slotStart;
    public int currentLine;
    public Value[] locals;

    public CallFrame(UserFunction function, ChunkReader reader, int slotStart, int arity) {
        this.function = function;
        this.reader = reader;
        this.ip = 0;
        this.slotStart = slotStart;
        this.locals = new Value[Math.max(arity, 4)];
    }

    public Value getLocal(int slot) {
        if (slot < 0 || slot >= locals.length) return Value.NULL;
        Value val = locals[slot];
        return val != null ? val : Value.NULL;
    }

    public void setLocal(int slot, Value value) {
        if (slot >= locals.length) {
            Value[] newLocals = new Value[slot + 1];
            System.arraycopy(locals, 0, newLocals, 0, locals.length);
            locals = newLocals;
        }
        locals[slot] = value;
    }
}
