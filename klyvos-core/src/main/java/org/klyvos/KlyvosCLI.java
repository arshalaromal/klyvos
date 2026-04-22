package org.klyvos;

import org.klyvos.bin.ChunkIO;
import org.klyvos.vm.Chunk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Arshal Aromal
 */
public class KlyvosCLI {
    private static final String VERSION = "Klyvos v0.1.0";

    public static void main(String[] args) {

        if (args.length == 0) {
            printHelp();
            return;
        }
        switch (args[0]) {
            case "-v", "-version" -> System.out.println(VERSION);
            case "-h", "--help" -> printHelp();
            case "compile" -> {
                if (args.length != 2) {
                    System.err.println("Usage: klyvos compile <file.klvs>");
                    return;
                }
                String sourceFile = args[1];
                if (!sourceFile.endsWith(".klvs")) {
                    System.err.println("Expected a .klvs source file.");
                    return;
                }
                try {
                    compileToFile(sourceFile);
                } catch (IOException e) {
                    System.err.println("Invalid File");
                }
            }
            case "run" -> {
                if (args.length != 2) {
                    System.out.println(args);
                    System.err.println("Usage: klyvos run <file.klvs>");
                    return;
                }
                String sourceFile = args[1];
                if (!sourceFile.endsWith(".klvs")) {
                    System.err.println("Expected a .klvs source file.");
                    return;
                }
                try {
                    runSource(sourceFile);
                } catch (IOException e) {
                    System.err.println("Invalid File");
                }
            }
            case "dis" -> {
                if (args.length != 2) {
                    System.err.println("Usage: klyvos dis <file.klvb>");
                    return;
                }
                try {
                    disassemble(args[1]);
                } catch (IOException e) {
                    System.err.println("Invalid File");
                }
            }

            default -> {
                String file = args[0];
                if (file.endsWith(".klvb")) {
                    try {
                        runBytecode(file);
                    } catch (IOException e) {
                        System.err.println("Invalid File");
                    }
                } else {
                    System.err.println("Unknown command or unsupported file: " + file);
                    printHelp();
                }
            }

        }
    }

    private static void compileToFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        String source = Files.readString(path);
        String name = path.getFileName().toString();
        KlyvosEngine engine = new KlyvosEngine(path.toAbsolutePath().getParent());


        byte[] bytecode = engine.compileSource(source, name);
        if (bytecode != null) {
            Path out = Path.of(filePath.replace(".klvs", ".klvb"));
            Files.write(out, bytecode);
        }

    }

    private static byte[] compile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        String source = Files.readString(path);
        String name = path.getFileName().toString();
        KlyvosEngine engine = new KlyvosEngine(path.toAbsolutePath().getParent());

        return engine.compileSource(source, name);

    }

    private static void runSource(String filePath) throws IOException {

        byte[] bytecode = compile(filePath);
        if (bytecode != null) {
            Path out = Path.of(filePath.replace(".klvs", ".klvb"));
            Files.write(out, bytecode);
            runBytecode(out.toAbsolutePath().toString());
        }
    }

    private static void runBytecode(String path) throws IOException {
        Path filePath = Path.of(path);
        byte[] bytecode = Files.readAllBytes(filePath);
        KlyvosEngine engine = new KlyvosEngine(filePath.toAbsolutePath().getParent());

        engine.runBytecode(bytecode);
    }

    private static void disassemble(String path) throws IOException {
        byte[] bytecode = Files.readAllBytes(Path.of(path));
        Chunk chunk = ChunkIO.deserializeWithHeader(bytecode);
        Disassembler.disassembleChunk(chunk);
    }


    private static void printHelp() {
        System.out.println("""
                ╔══════════════════════════════════════════════╗
                ║              Klyvos Language CLI             ║
                ╚══════════════════════════════════════════════╝
                
                Klyvos is a modern, lightweight programming language
                designed for high performance, embeddability, and modular
                architecture. It compiles to efficient bytecode for fast
                interpretation and supports native extensions and modular tooling.
                
                Usage:
                  klyvos run <file.klvs>      Compile and run a Klyvos source file
                                              (auto-compiles required modules)
                
                  klyvos compile <file.klvs>  Compile source to bytecode (.klvb)
                                              without executing
                
                  klyvos <file.klvb>          Run a precompiled Klyvos bytecode file
                
                  klyvos dis <file.klvb>      Disassemble and print bytecode contents
                
                  klyvos -v | -version        Show Klyvos version
                
                  klyvos -h | --help          Show this help information
                
                Notes:
                - `.klvs` → Klyvos source code
                - `.klvb` → Compiled bytecode format
                
                Examples:
                  klyvos compile myscript.klvs
                  klyvos run test.klvs
                  klyvos dis output.klvb
                """);
    }


}
