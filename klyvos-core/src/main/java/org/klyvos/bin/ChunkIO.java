package org.klyvos.bin;

import org.klyvos.vm.Chunk;
import org.klyvos.vm.UserFunction;
import org.klyvos.vm.Value;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ChunkIO: Handles serialization and deserialization of Chunk to/from byte[].
 * Now supports robust serialization of user functions (including default value chunks)
 * and lists containing functions.
 */
public class ChunkIO {
    // Magic header for file format identification
    private static final byte[] HEADER = {'K', 'V', 'B', 'S', 1}; // Version 1

    /**
     * Serializes a Chunk into a byte array with a header.
     */
    public static byte[] serializeWithHeader(Chunk chunk) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(HEADER);
        writeChunk(new DataOutputStream(baos), chunk);
        return baos.toByteArray();
    }

    /**
     * Deserializes a Chunk from a byte array with a header.
     */
    public static Chunk deserializeWithHeader(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(bais);

        // Check header
        for (byte b : HEADER) {
            if (in.readByte() != b) {
                throw new IOException("Invalid Klyvos bytecode header");
            }
        }
        return readChunk(in);
    }

    // --- Internal Serialization Helpers ---

    private static void writeChunk(DataOutputStream out, Chunk chunk) throws IOException {
        // Write name
        out.writeUTF(chunk.getName());

        // Write code
        int codeSize = chunk.size();
        out.writeInt(codeSize);
        for (int i = 0; i < codeSize; i++) {
            out.writeByte(chunk.getCode(i));
            out.writeInt(chunk.getLine(i));
        }

        // Write constants
        List<Value> constants = chunk.getConstants();
        out.writeInt(constants.size());
        for (Value val : constants) {
            writeValue(out, val);
        }
    }

    private static Chunk readChunk(DataInputStream in) throws IOException {
        String name = in.readUTF();
        Chunk chunk = new Chunk(name);

        // Read code
        int codeSize = in.readInt();
        for (int i = 0; i < codeSize; i++) {
            byte b = in.readByte();
            int line = in.readInt();
            chunk.write(b, line);
        }

        // Read constants
        int constCount = in.readInt();
        List<Value> constants = new ArrayList<>();
        for (int i = 0; i < constCount; i++) {
            constants.add(readValue(in));
        }
        chunk.setConstants(constants);

        return chunk;
    }

    // --- Value Serialization ---

    private static void writeValue(DataOutputStream out, Value value) throws IOException {
        out.writeByte(value.type.ordinal());
        switch (value.type) {
            case INT -> out.writeLong(value.asInt());
            case DECIMAL -> out.writeDouble(value.asDecimal());
            case BOOL -> out.writeBoolean(value.asBool());
            case STRING -> out.writeUTF(value.asString());

            case LIST -> {
                List<Value> list = value.asList();
                out.writeInt(list.size());
                for (Value v : list) writeValue(out, v); // recursive
            }

            case USER_FN -> {
                // Serialize: name, param names, default arg chunks, main chunk
                UserFunction fn = value.asUserFn();
                out.writeUTF(fn.getName());
                List<String> params = fn.getParamNames();
                out.writeInt(params.size());
                for (String p : params) out.writeUTF(p);

                // Default chunks
                List<Chunk> defChunks = fn.getDefaultChunks();
                out.writeInt(defChunks.size());
                for (Chunk dc : defChunks) {
                    if (dc == null) {
                        out.writeBoolean(false);
                    } else {
                        out.writeBoolean(true);
                        writeChunk(out, dc);
                    }
                }
                // Main function chunk
                writeChunk(out, fn.getChunk());
            }

            case NULL -> {/* nothing to write */}

            // For unsupported types, throw for now (native fn, modules, error, etc)
            default -> throw new IOException("Unsupported value type for serialization: " + value.type);
        }
    }

    private static Value readValue(DataInputStream in) throws IOException {
        int typeOrdinal = in.readByte();
        Value.Type type = Value.Type.values()[typeOrdinal];
        return switch (type) {
            case INT -> Value.ofInt(in.readLong());
            case DECIMAL -> Value.ofDecimal(in.readDouble());
            case BOOL -> Value.ofBool(in.readBoolean());
            case STRING -> Value.ofString(in.readUTF());

            case LIST -> {
                int n = in.readInt();
                List<Value> vals = new ArrayList<>(n);
                for (int i = 0; i < n; i++) vals.add(readValue(in));
                yield Value.ofList(vals);
            }

            case USER_FN -> {
                String name = in.readUTF();
                int paramCount = in.readInt();
                List<String> params = new ArrayList<>(paramCount);
                for (int i = 0; i < paramCount; i++) params.add(in.readUTF());
                int defCount = in.readInt();
                List<Chunk> defChunks = new ArrayList<>(defCount);
                for (int i = 0; i < defCount; i++) {
                    boolean hasChunk = in.readBoolean();
                    defChunks.add(hasChunk ? readChunk(in) : null);
                }
                Chunk chunk = readChunk(in);
                yield Value.ofUserFunction(new UserFunction(name, params, defChunks, chunk));
            }

            case NULL -> Value.NULL;
            default -> throw new IOException("Unsupported value type for deserialization: " + type);
        };
    }
}