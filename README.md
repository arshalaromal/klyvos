# Klyvos

Klyvos is a programming language built in Java with a custom compiler, bytecode format, and stack-based virtual machine.

## Pipeline

```text
source (.klvs) -> ANTLR -> parse tree -> AST -> bytecode (.klvb) -> VM
```

## Features

* Source files: `.klvs`
* Bytecode files: `.klvb`
* Stack-based VM
* Functions
* `var` and `const`
* `if`, `while`, `for`
* `break`, `continue`, `return`
* Lists, indexing, and slicing
* Bytecode disassembly

## Basic syntax

```klyvos
const a = 1
var b = 0
```

Klyvos uses `var` for mutable values and `const` for values that should not be reassigned.

## Execution model

Klyvos follows a compile-then-run workflow:

1. Write source code in a `.klvs` file
2. Parse it with ANTLR
3. Build the AST
4. Compile the AST into bytecode
5. Run the bytecode on the VM

The VM is stack-based, so instructions operate on values pushed to and popped from a stack.

## Syntax overview

### Statements

* `use`
* `var`
* `const`
* `fn`
* expression statements
* `if`
* `while`
* `for`
* `break`
* `continue`
* blocks
* `return`

### Expressions

* assignment
* logical operators: `and`, `or`, `not`
* comparisons: `==`, `!=`, `<`, `<=`, `>`, `>=`, `in`
* arithmetic: `+`, `-`, `*`, `/`, `%`, `**`
* unary operators
* function calls
* indexing and slicing
* list literals
* literals and variables

## Example programs

### Variables and arithmetic

```klyvos
const a = 10
const b = 5

println(a + b)
```

### Loop

```klyvos
var i = 0

while (i < 5) {
    i = i + 1
}
```

### For loop

```klyvos
for (i in [1, 2, 3, 4, 5]) {
    println(i)
}
```

## Bytecode

Klyvos bytecode is stored in `.klvb` files.
Klyvos compiles high-level code into a compact instruction set executed by a stack-based virtual machine.

### Stack and variable access

| Mnemonic       | Code   |
|----------------|--------|
| `LOAD_CONST`   | `0x01` |
| `LOAD_VAR`     | `0x02` |
| `STORE_VAR`    | `0x03` |
| `LOAD_GLOBAL`  | `0x04` |
| `STORE_GLOBAL` | `0x05` |

### Arithmetic and math

| Mnemonic | Code   |
|----------|--------|
| `ADD`    | `0x10` |
| `SUB`    | `0x11` |
| `MUL`    | `0x12` |
| `DIV`    | `0x13` |
| `MOD`    | `0x14` |
| `NEG`    | `0x15` |

### Logic and boolean

| Mnemonic | Code   |
|----------|--------|
| `EQ`     | `0x20` |
| `NEQ`    | `0x21` |
| `LT`     | `0x22` |
| `LTE`    | `0x23` |
| `GT`     | `0x24` |
| `GTE`    | `0x25` |
| `AND`    | `0x26` |
| `OR`     | `0x27` |
| `NOT`    | `0x28` |

### Control flow

| Mnemonic        | Code   |
|-----------------|--------|
| `JUMP`          | `0x30` |
| `JUMP_IF_FALSE` | `0x31` |
| `BREAK`         | `0x32` |
| `CONTINUE`      | `0x33` |
| `CALL`          | `0x34` |
| `RETURN`        | `0x35` |
| `POP`           | `0x36` |

### Lists and slicing

| Mnemonic    | Code   |
|-------------|--------|
| `MAKE_LIST` | `0x40` |
| `GET_INDEX` | `0x41` |
| `SET_INDEX` | `0x42` |
| `SLICE`     | `0x43` |

### Utilities

| Mnemonic | Code   |
|----------|--------|
| `NOOP`   | `0x60` |
| `DUP`    | `0x61` |
| `SWAP`   | `0x62` |

## CLI

## File types

- `.klvs` — source code
- `.klvb` — compiled bytecode

```text
klyvos run <file.klvs>      Compile and run a source file
klyvos compile <file.klvs>  Compile source to bytecode only
klyvos <file.klvb>          Run a bytecode file
klyvos dis <file.klvb>      Disassemble bytecode
klyvos -v | -version        Show version
klyvos -h | --help          Show help
```

### Examples

```bash
klyvos compile myscript.klvs
klyvos run test.klvs
klyvos dis output.klvb
```

## Project structure

* `ast/` — AST nodes and visitors
* `parser/` — AST builder
* `compiler/` — compiler and scope logic
* `vm/` — VM and runtime values
* `bin/` — bytecode I/O helpers
* `tokens/` — tokens and token types
* `validator/` — validation layer
* `modules/` — module registry
* `error/` — compile and runtime errors

## Runtime requirement

Klyvos requires a `stdlib` directory at runtime.

When running the executable or CLI, place the `stdlib` folder in the same directory as the program:

```text
klyvos.exe
stdlib/
```
## Build

```bash
mvn clean package
```

## License

[License](https://github.com/arshalaromal/klyvos/blob/main/LICENSE)
