package org.klyvos.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chunk {
    private final String name;

    public List<Byte> getCode() {
        return code;
    }

    private final List<Byte> code = new ArrayList<>();
    private final List<Value> constants = new ArrayList<>();
    private final List<Integer> lines = new ArrayList<>();
    private final Map<Value, Integer> dedupConstMap = new HashMap<>();

    public Chunk(String name) {
        this.name = name;
    }

    public int addConstant(Value value) {
        if (dedupConstMap.containsKey(value))
            return dedupConstMap.get(value);
        int idx = constants.size();
        constants.add(value);
        dedupConstMap.put(value, idx);
        return idx;
    }

    public Value getConstant(int idx) {
        return constants.get(idx);
    }

    public List<Value> getConstants() {
        return constants;
    }

    public int codeSize() {
        return code.size();
    }

    public int size() {
        return code.size();
    }

    public byte getCode(int i) {
        return code.get(i);
    }

    public int getLine(int i) {
        return lines.get(i);
    }

    public void write(byte b, int line) {
        code.add(b);
        lines.add(line);
    }

    public void emit(byte b, int line) {
        write(b, line);
    }

    // --- Jumps ---
    public int writeJump(byte opcode, int line) {
        write(opcode, line);
        write((byte) 0, line); // placeholder hi
        write((byte) 0, line); // placeholder lo
        return code.size() - 2;
    }

    public void patchJump(int loc, int offset) {
        code.set(loc, (byte) ((offset >> 8) & 0xFF));
        code.set(loc + 1, (byte) (offset & 0xFF));
    }

    // For compiler
    public void emitShort(int value, int line) {
        write((byte) ((value >> 8) & 0xFF), line);
        write((byte) (value & 0xFF), line);
    }

    public void emitConst(Value v, int line) {
        int idx = addConstant(v);
        emitShort(idx, line);
    }

    public int readShort(int ip) {
        int hi = Byte.toUnsignedInt(code.get(ip));
        int lo = Byte.toUnsignedInt(code.get(ip + 1));
        return (hi << 8) | lo;
    }

    public String getName() {
        return name;
    }

    public void setConstants(List<Value> constants) {
        this.constants.clear();
        this.constants.addAll(constants);
        // If you have dedupConstMap, rebuild it here as well!
        this.dedupConstMap.clear();
        for (int i = 0; i < constants.size(); i++) {
            this.dedupConstMap.put(constants.get(i), i);
        }
    }
}