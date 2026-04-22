package org.klyvos;

import org.klyvos.compiler.OpCode;
import org.klyvos.vm.Chunk;
import org.klyvos.vm.UserFunction;
import org.klyvos.vm.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Future-proof, fast, memory-efficient, and simple disassembler for Klyvos bytecode.
 */
public class Disassembler {
    /**
     * Entry point: disassemble the top-level chunk.
     */
    public static void disassembleChunk(Chunk chunk) {
        Set<Chunk> visited = new HashSet<>();
        disassembleChunk(chunk, null, "", visited);
    }

    /**
     * Disassembles the given chunk.
     *
     * @param chunk   Bytecode chunk to disassemble
     * @param fn      The UserFunction, if this chunk is a function body (null for top-level)
     * @param indent  Indentation for nested display
     * @param visited Chunks already visited (to avoid cycles)
     */
    private static void disassembleChunk(Chunk chunk, UserFunction fn,
                                         String indent, Set<Chunk> visited) {
        if (!visited.add(chunk)) return;

        // Header
        System.out.printf("\n%s== Disassembling chunk: %s ==\n",
                indent, chunk.getName());

        int ip = 0;
        while (ip < chunk.size()) {
            int offset = ip;
            byte instr = chunk.getCode(ip++);
            OpCode op;
            try {
                op = OpCode.fromByte(instr);
            } catch (IllegalArgumentException e) {
                System.out.printf("%s%04d  UNKNOWN (%d)\n", indent, offset, instr);
                continue;
            }

            String lineInfo = (offset < chunk.size())
                    ? " [line " + chunk.getLine(offset) + "]" : "";

            System.out.printf("%s%04d  %-16s", indent, offset, op.name());

            switch (op) {
                // ─── Stack & Variable Access ─────────────────────────
                case LOAD_CONST, LOAD_GLOBAL, STORE_GLOBAL, USE_MODULE, GET_PROP, SET_PROP: {
                    int idx = chunk.readShort(ip);
                    ip += 2;
                    Value c = chunk.getConstant(idx);
                    System.out.printf("%5d → %s%s\n", idx, summarizeValue(c), lineInfo);
                    break;
                }
                case LOAD_VAR:
                case STORE_VAR: {
                    int slot = chunk.readShort(ip);
                    ip += 2;
                    String name = resolveLocalName(fn, slot);
                    System.out.printf("%5d → %s%s\n", slot, name, lineInfo);
                    break;
                }

                // ─── Arithmetic & Math & Logic ────────────────────────
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case MOD:
                case NEG:
                case EXP:
                case EQ:
                case NEQ:
                case LT:
                case LTE:
                case GT:
                case GTE:
                case NOT:
                case IN: {
                    System.out.println(lineInfo);
                    break;
                }

                // ─── Control Flow ────────────────────────────────────
                case JUMP:
                case JUMP_IF_FALSE: {
                    int target = chunk.readShort(ip);
                    ip += 2;
                    System.out.printf("%5d%s\n", target, lineInfo);
                    break;
                }
                case BREAK:
                case CONTINUE:
                case RETURN:
                case POP:
                case HALT:
                case NOOP:
                case DUP:
                case SWAP: {
                    System.out.println(lineInfo);
                    break;
                }

                case CALL: {
                    int argc = chunk.readShort(ip);
                    ip += 2;
                    System.out.printf("%5d%s\n", argc, lineInfo);
                    break;
                }

                // ─── Lists & Slicing ──────────────────────────────────
                case MAKE_LIST: {
                    int count = Byte.toUnsignedInt(chunk.getCode(ip++));
                    System.out.printf(" count=%d%s\n", count, lineInfo);
                    break;
                }
                case GET_INDEX:
                case SET_INDEX:
                case SLICE: {
                    System.out.println(lineInfo);
                    break;
                }

                // ─── Native & Method Calls ───────────────────────────
                case NATIVE_CALL: {
                    int modIdx = chunk.readShort(ip);
                    ip += 2;
                    int fnIdx = chunk.readShort(ip);
                    ip += 2;
                    int argCount = chunk.readShort(ip);
                    ip += 2;
                    String modName = chunk.getConstant(modIdx).asString();
                    String fnName = chunk.getConstant(fnIdx).asString();
                    System.out.printf(" %s.%s argc=%d%s\n", modName, fnName, argCount, lineInfo);
                    break;
                }
                case CALL_METHOD: {
                    int nameIdx = chunk.readShort(ip);
                    ip += 2;
                    int argCount = chunk.readShort(ip);
                    ip += 2;
                    String method = chunk.getConstant(nameIdx).asString();
                    System.out.printf(" %s argc=%d%s\n", method, argCount, lineInfo);
                    break;
                }

                default: {
                    System.out.printf(" (unhandled)%s\n", lineInfo);
                }
            }
        }

        // Constants table
        System.out.printf("\n%sConstants:\n", indent);
        for (int i = 0; i < chunk.getConstants().size(); i++) {
            System.out.printf("%s[%02d] %s\n",
                    indent, i,
                    summarizeValue(chunk.getConstants().get(i)));
        }

        // Recurse into nested user functions and their default arg chunks
        for (Value v : chunk.getConstants()) {
            if (v.isUserFn()) {
                UserFunction fnVal = v.asUserFn();
                // Disassemble the function body
                disassembleChunk(fnVal.getChunk(), fnVal, indent + "  ", visited);
                // Then its default-arg chunks
                List<Chunk> defs = fnVal.getDefaultChunks();
                for (int d = 0; defs != null && d < defs.size(); d++) {
                    Chunk dc = defs.get(d);
                    if (dc != null) {
                        System.out.printf("\n%sDefault arg chunk %d for %s:\n",
                                indent, d, fnVal.getName());
                        disassembleChunk(dc, fnVal, indent + "  ", visited);
                    }
                }
            }
        }
    }

    /**
     * Read big-endian short from code list starting at ip.
     */
    private static int readShort(Chunk chunk, int ip) {
        int hi = Byte.toUnsignedInt(chunk.getCode(ip));
        int lo = Byte.toUnsignedInt(chunk.getCode(ip + 1));
        return (hi << 8) | lo;
    }

    /**
     * Summarize a constant Value for display.
     */
    private static String summarizeValue(Value v) {
        if (v == null || v.isNull()) return "null";
        if (v.isUserFn()) {
            UserFunction fn = v.asUserFn();
            return "<fn " + fn.getName() +
                    "(" + String.join(", ", fn.getParamNames()) + ")>";
        }
        return v.toString();
    }

    /**
     * Resolve a slot index to a local name or fallback.
     */
    private static String resolveLocalName(UserFunction fn, int slot) {
        if (fn != null && slot < fn.getParamNames().size()) {
            return fn.getParamNames().get(slot);
        }
        return "local" + slot;
    }
}