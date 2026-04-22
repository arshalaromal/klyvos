package org.klyvos.vm;

import java.util.List;

public class UserFunction {
    private final String name;
    private final Chunk chunk;
    private final List<String> paramNames;

    public List<Chunk> getDefaultChunks() {
        return defaultChunks;
    }

    private final List<Chunk> defaultChunks;

    public UserFunction(String name, List<String> paramNames, List<Chunk> defaultChunks, Chunk chunk) {
        this.name = name;
        this.chunk = chunk;
        this.paramNames = paramNames;
        this.defaultChunks = defaultChunks;
    }

    public String getName() {
        return name;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public int getArity() {
        return paramNames.size();
    }

    public Chunk getDefaultChunk(int i) {
        return (defaultChunks != null && i < defaultChunks.size()) ? defaultChunks.get(i) : null;
    }

    public List<String> getParamNames() {
        return paramNames;
    }
}