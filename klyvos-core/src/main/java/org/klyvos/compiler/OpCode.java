package org.klyvos.compiler;

/**
 * @author Arshal Aromal
 */

public enum OpCode {
    HALT((byte) 0x00),
    // ─── Stack & Variable Access ─────────────────────
    LOAD_CONST((byte) 0x01),
    LOAD_VAR((byte) 0x02),
    STORE_VAR((byte) 0x03),
    LOAD_GLOBAL((byte) 0x04),
    STORE_GLOBAL((byte) 0x05),

    // ─── Arithmetic & Math ───────────────────────────
    ADD((byte) 0x10),
    SUB((byte) 0x11),
    MUL((byte) 0x12),
    DIV((byte) 0x13),
    MOD((byte) 0x14),
    NEG((byte) 0x15),
    EXP((byte) 0x16),

    // ─── Logical & Boolean ───────────────────────────
    EQ((byte) 0x20),
    NEQ((byte) 0x21),
    LT((byte) 0x22),
    LTE((byte) 0x23),
    GT((byte) 0x24),
    GTE((byte) 0x25),
    NOT((byte) 0x28),
    IN((byte) 0x19),

    // ─── Control Flow ────────────────────────────────
    JUMP((byte) 0x30),
    JUMP_IF_FALSE((byte) 0x31),
    BREAK((byte) 0x32),
    CONTINUE((byte) 0x33),
    CALL((byte) 0x34),
    RETURN((byte) 0x35),
    POP((byte) 0x36),

    // ─── Lists & Slicing ─────────────────────────────
    MAKE_LIST((byte) 0x40),
    GET_INDEX((byte) 0x41),
    SET_INDEX((byte) 0x42),
    SLICE((byte) 0x43),

    // ─── Native Interop & Objects ────────────────────
    NATIVE_CALL((byte) 0x50),
    GET_PROP((byte) 0x51),
    SET_PROP((byte) 0x52),
    CALL_METHOD((byte) 0x53),

    // ─── Utilities ───────────────────────────────────
    USE_MODULE((byte) 0x59),
    NOOP((byte) 0x60),
    DUP((byte) 0x61),
    SWAP((byte) 0x62);

    private final byte code;


    OpCode(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static OpCode fromByte(byte code) {
        for (OpCode opCode : OpCode.values()) {
            if (opCode.code == code)
                return opCode;
        }
        throw new IllegalArgumentException("Unknown OpCode: " + code);
    }
}
