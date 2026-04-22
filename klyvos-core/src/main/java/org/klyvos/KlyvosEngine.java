package org.klyvos;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.klyvos.ast.ASTNode;
import org.klyvos.bin.ChunkIO;
import org.klyvos.compiler.Compiler;
import org.klyvos.error.KlyvosCompileError;
import org.klyvos.error.KlyvosError;
import org.klyvos.modules.ModuleRegistry;
import org.klyvos.parser.ASTBuilder;
import org.klyvos.parser.KlyvosLexer;
import org.klyvos.parser.KlyvosParser;
import org.klyvos.vm.Chunk;
import org.klyvos.vm.Environment;
import org.klyvos.vm.VM;
import org.klyvos.vm.Value;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KlyvosEngine {
    private final ModuleRegistry registry;
    private final Map<String, Value> preloadedGlobals;

    public KlyvosEngine(Path userDir) {
        this(userDir, false);
    }

    public static Path getExecutablePath() {
        try {
            File codeSource = new File(
                    KlyvosEngine.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            );

            // If running from jar or exe, return the directory it's in
            if (codeSource.isFile()) {
                return codeSource.getParentFile().toPath().toAbsolutePath();
            }

            // If running from classes dir (e.g., IDE), return the project root
            // e.g., /target/classes → go up two levels
            return codeSource.toPath().getParent().getParent().getParent().toAbsolutePath();

        } catch (URISyntaxException e) {
            // Fallback: working directory
            return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        }
    }

    public KlyvosEngine(Path userDir, boolean skipTopLevel) {
        Path executablePath = getExecutablePath();

        Path stdlibDir = executablePath.resolve("stdlib");
        Path globalDir = executablePath.resolve("stdlib/globals");
        this.registry = new ModuleRegistry(userDir, stdlibDir, globalDir);

        // Register all native functions before doing anything else
        registry.registerBuiltInNatives();

        Environment preload = new Environment();
        registry.injectNativeModules(preload);

        if (!skipTopLevel) {
            registry.loadTopLevelGlobals(preload);
        }

        this.preloadedGlobals = new HashMap<>(preload.getVariables());
    }

    public byte[] compileSource(String source, String name) {
        List<KlyvosError> errors = new ArrayList<>();
        ASTNode ast = parseSourceToAST(source, errors);

        if (ast == null || !errors.isEmpty()) {
            errors.forEach(e -> System.err.println(e.format()));
            return null;
        }

        Chunk chunk = new Compiler(new Chunk(name), errors, name, registry, preloadedGlobals).compile(ast);
        if (!errors.isEmpty()) {
            errors.forEach(e -> System.err.println(e.format()));
            return null;
        }

        try {
            return ChunkIO.serializeWithHeader(chunk);
        } catch (IOException e) {
            System.err.println("Serialize error: " + e);
            return null;
        }
    }

    public void runBytecode(byte[] code) {
        try {
            Chunk chunk = ChunkIO.deserializeWithHeader(code);
            VM vm = new VM(registry);
            registry.injectNativeModules(vm.getGlobals());
            registry.loadTopLevelGlobals(vm.getGlobals());

            for (var entry : registry.getNativeFunctionMap().entrySet()) {
                for (var fn : entry.getValue().entrySet()) {
                    vm.registerNative(entry.getKey(), fn.getKey(), fn.getValue());
                }
            }

            Value result = vm.runTopLevel(chunk);
            if (result.isError()) {
                throw result.asError();
            } else {
                //System.out.println(result);
            }
        } catch (IOException e) {
            System.err.println("Bytecode load failed: " + e);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println();
        }
    }

    private ASTNode parseSourceToAST(String src, List<KlyvosError> errors) {
        try {
            CharStream input = CharStreams.fromString(src);
            KlyvosLexer lexer = new KlyvosLexer(input);
            KlyvosParser parser = new KlyvosParser(new CommonTokenStream(lexer));
            return new ASTBuilder().visit(parser.program());
        } catch (Exception ex) {
            errors.add(new KlyvosCompileError("ParseError", ex.getMessage(), -1));
            return null;
        }
    }


}
