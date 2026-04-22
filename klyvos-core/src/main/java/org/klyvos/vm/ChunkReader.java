package org.klyvos.vm;

public class ChunkReader {
    public Chunk getChunk() {
        return chunk;
    }

    public boolean hasNext() {
        return ip < chunk.size();
    }

    private final Chunk chunk;
    public int ip;

    public ChunkReader(Chunk chunk) {
        this.chunk = chunk;
        this.ip = 0;
    }

    public byte next() {
        return chunk.getCode(ip++);
    }

    public int readShort() {
        int hi = Byte.toUnsignedInt(chunk.getCode(ip++));
        int lo = Byte.toUnsignedInt(chunk.getCode(ip++));
        return (hi << 8) | lo;
    }

    public byte readByte() {
        return next();
    }

    public int getIP() {
        return ip;
    }

    public void setIP(int ip) {
        this.ip = ip;
    }
}