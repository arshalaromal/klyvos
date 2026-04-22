package org.klyvos.modules;

import org.klyvos.KlyvosEngine;
import org.klyvos.bin.ChunkIO;
import org.klyvos.error.KlyvosRuntimeError;
import org.klyvos.vm.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ModuleRegistry {
    public enum Type {
        NATIVE,
        SCRIPT
    }

    private final Map<String, Type> types = new HashMap<>();

    public Type getModuleType(String name) {
        if (types.containsKey(name)) return types.get(name);
        if (nativeFunctionMap.containsKey(name)) return Type.NATIVE;
        return null;
    }

    private final Path userDir;
    private final Path stdlibDir;
    private final Path globalDir;
    private final Map<String, Chunk> scriptModules = new HashMap<>();
    private final Map<String, Map<String, NativeFunction>> nativeFunctionMap = new HashMap<>();

    public ModuleRegistry(Path userDir, Path stdlibDir, Path globalDir) {
        this.userDir = userDir;
        this.stdlibDir = stdlibDir;
        this.globalDir = globalDir;
    }

    public void registerBuiltInNatives() {
        registerNative("console", "write", args -> {

            System.out.print(args.isEmpty() ? "" : args.getFirst().toString());
            return Value.NULL;
        });

        registerNative("console", "read", args -> {
            if (args.size() > 1)
                throw new KlyvosRuntimeError("InvalidArguments", "Expected 0 or 1 argument", -1);

            if (!args.isEmpty())
                System.out.print(args.get(0));

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = br.readLine();
            return Value.ofString(s);
        });


        registerNative("core", "isNumber", args -> {
            if (args.size() != 1)
                throw new KlyvosRuntimeError("InvalidArguments", "Expected only 1 argument", -1);
            return Value.ofBool(args.get(0).isInt());
        });

    }

    public void registerNative(String mod, String fn, NativeFunction impl) {
        nativeFunctionMap.computeIfAbsent(mod, k -> new HashMap<>()).put(fn, impl);
        types.put(mod, Type.NATIVE); // 🔧 Mark as native
    }

    public Map<String, NativeFunction> getNativeFunctions(String module) {
        return nativeFunctionMap.getOrDefault(module, Map.of());
    }

    public Map<String, Map<String, NativeFunction>> getNativeFunctionMap() {
        return nativeFunctionMap;
    }

    public void injectNativeModules(Environment env) {
        for (var entry : nativeFunctionMap.entrySet()) {
            env.define(entry.getKey(), Value.of(new ModuleNamespace(entry.getKey(), Map.of())));
        }
    }

    public void loadTopLevelGlobals(Environment env) {
        injectNativeModules(env);

        try (Stream<Path> files = Files.list(globalDir)) {
            for (Path file : files.filter(f -> f.toString().endsWith(".klvs")).toList()) {
                String name = file.getFileName().toString().replace(".klvs", "");
                try {
                    Chunk chunk = compileAndCache(file, name, true);
                    VM vm = new VM(this);
                    injectNativeModules(vm.getGlobals());
                    for (var modEntry : nativeFunctionMap.entrySet()) {
                        for (var fn : modEntry.getValue().entrySet()) {
                            vm.registerNative(modEntry.getKey(), fn.getKey(), fn.getValue());
                        }
                    }
                    Value result = vm.runTopLevel(chunk);
                    if (result.isError()) System.err.println(result.asError().getMessage());
                    env.merge(vm.getGlobals());
                } catch (Exception ex) {
                    System.err.println("Top-level load error in " + name + ": " + ex.getMessage());
                }
            }
        } catch (IOException ignored) {
        }
    }

    public Chunk loadScriptModule(String name, int line) throws IOException {
        if (scriptModules.containsKey(name)) return scriptModules.get(name);

        for (Path base : List.of(userDir, stdlibDir)) {
            Path klvb = base.resolve(name + ".klvb");
            if (Files.exists(klvb)) {
                types.put(name, Type.SCRIPT);
                return scriptModules.computeIfAbsent(name,
                        _ -> safeDeserialize(klvb));

            }

            Path klvs = base.resolve(name + ".klvs");
            if (Files.exists(klvs)) {
                types.put(name, Type.SCRIPT);

                return compileAndCache(klvs, name, false);
            }
        }

        throw new KlyvosRuntimeError("NameError", "Module not found: " + name, line);
    }

    private Chunk compileAndCache(Path path, String name, boolean isTopLevel) throws IOException {
        String src = Files.readString(path);
        KlyvosEngine engine = new KlyvosEngine(userDir, isTopLevel);
        byte[] code = engine.compileSource(src, name);
        Chunk chunk = ChunkIO.deserializeWithHeader(code);
        scriptModules.put(name, chunk);

        try {
            Files.write(path.resolveSibling(name + ".klvb"), ChunkIO.serializeWithHeader(chunk));
        } catch (IOException ignored) {
        }

        return chunk;
    }

    private Chunk safeDeserialize(Path path) {
        try {
            return ChunkIO.deserializeWithHeader(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read module bytecode: " + path);
        }
    }
}
