package org.klyvos.compiler;

import org.klyvos.ast.*;
import org.klyvos.ast.expressions.*;
import org.klyvos.ast.statements.*;
import org.klyvos.error.ErrorExpr;
import org.klyvos.error.ErrorStmt;
import org.klyvos.error.KlyvosCompileError;
import org.klyvos.error.KlyvosError;
import org.klyvos.modules.ModuleRegistry;
import org.klyvos.tokens.TokenType;
import org.klyvos.vm.Chunk;
import org.klyvos.vm.ModuleNamespace;
import org.klyvos.vm.UserFunction;
import org.klyvos.vm.Value;

import java.util.*;

public class Compiler implements ASTVisitor<Void> {
    private final Map<String, Value> globals;
    private final Deque<List<Integer>> breakJumpStack = new ArrayDeque<>();
    private final Deque<List<Integer>> continueJumpStack = new ArrayDeque<>();
    private final ModuleRegistry modules;
    private final Map<String, ModuleRegistry.Type> importedModules = new HashMap<>();
    private final Chunk chunk;
    private final List<KlyvosError> errors;
    private final String currentName;

    private int currentLine = -1;

    private final Deque<Scope> scopeStack = new ArrayDeque<>();

    private static class Scope {
        final boolean isFunctionScope;
        final Map<String, Integer> slotMap = new HashMap<>();
        int nextSlot = 0;

        Scope(boolean isFunctionScope) {
            this.isFunctionScope = isFunctionScope;
        }
    }

    public Compiler(Chunk chunk, List<KlyvosError> errors, String name, ModuleRegistry modules, Map<String, Value> globals) {
        this.chunk = chunk;
        this.errors = errors;
        this.currentName = name;
        this.modules = modules;
        scopeStack.push(new Scope(true)); // Top-level is a function-like scope

        this.globals = globals;
    }

    public Chunk compile(ASTNode node) {
        node.accept(this);
        return chunk;
    }


    private void setLine(int line) {
        this.currentLine = line;
    }

    private void emit(byte opcode) {
        chunk.write(opcode, currentLine);
    }

    private void emitBytes(byte b1, byte b2) {
        emit(b1);
        emit(b2);
    }

    private void emitShort(int value) {
        emit((byte) ((value >> 8) & 0xff));
        emit((byte) (value & 0xff));
    }

    private int makeConstant(Object value) {
        Value v = Value.of(value);
        List<Value> constants = chunk.getConstants();

        for (int i = 0; i < constants.size(); i++) {
            if (constants.get(i).type == v.type && Objects.equals(constants.get(i).value, v.value)) {
                return i;
            }
        }
        int index = chunk.addConstant(v);
        if (index > 0xFFFF) {
            errors.add(new KlyvosCompileError("Too many constants", currentName, currentLine));
            return 0;
        }
        return index;
    }

    private void emitConstant(Object value) {
        int index = makeConstant(value);
        emit(OpCode.LOAD_CONST.getCode());
        emitShort(index);
    }

    private void enterScope(boolean isFunction) {
        scopeStack.push(new Scope(isFunction));
    }

    private void exitScope() {
        scopeStack.pop();
    }

    int declareLocal(String name) {
        // Always add the variable to the current scope
        Scope current = scopeStack.peek();

        // Always allocate slot from nearest function scope
        Scope functionScope = null;
        for (Scope scope : scopeStack) {
            if (scope.isFunctionScope) {
                functionScope = scope;
                break;
            }
        }

        if (functionScope == null) {
            throw new IllegalStateException("No function scope found when declaring local");
        }

        int slot = functionScope.nextSlot++;

        if (current.slotMap.containsKey(name)) {
            System.err.println("Shadowing variable '" + name + "' in the same scope");
        }

        current.slotMap.put(name, slot);
        return slot;
    }


    private int getLocalSlot(String name) {
        for (Scope s : scopeStack) {
            if (s.slotMap.containsKey(name)) return s.slotMap.get(name);
        }
        return -1;
    }

    private boolean isLocal(String name) {
        return getLocalSlot(name) != -1;
    }

    private int emitJump(byte opcode) {
        emit(opcode);
        int pos = chunk.size();
        emitShort(0); // Reserve space for 2-byte absolute target
        return pos;
    }

    private void patchJump(int jumpLocation) {
        int target = chunk.size(); // Absolute bytecode position to jump to
        if (target > 0xFFFF) {
            errors.add(new KlyvosCompileError("Jump target too far", currentName, currentLine));
            return;
        }
        chunk.getCode().set(jumpLocation, (byte) ((target >> 8) & 0xff));
        chunk.getCode().set(jumpLocation + 1, (byte) (target & 0xff));
    }

    // --- AST Visitors ---

    @Override
    public Void visitProgram(Program program) {
        for (int i = 0; i < program.statements.size(); i++) {
            Statement stmt = program.statements.get(i);

            // If it's the last statement and an ExpressionStmt → compile without POP
            if (i == program.statements.size() - 1 && stmt instanceof ExpressionStmt exprStmt) {
                setLine(exprStmt.expression.line());
                exprStmt.expression.accept(this);
            } else {
                stmt.accept(this);
            }
        }

        emit(OpCode.HALT.getCode());
        return null;
    }

    @Override
    public Void visitLiteralExpr(LiteralExpr expr) {
        setLine(expr.value.line);
        emitConstant(expr.value.literal);
        return null;
    }

    @Override
    public Void visitVariableExpr(VariableExpr expr) {
        setLine(expr.name.line);

        String name = expr.name.lexeme;

        int slot = getLocalSlot(name);
        if (slot != -1) {
            emit(OpCode.LOAD_VAR.getCode());
            emitShort(slot);
        } else if (globals.containsKey(name)) {
            int index = makeConstant(name);
            emit(OpCode.LOAD_GLOBAL.getCode());
            emitShort(index);
        } else {
            errors.add(new KlyvosCompileError("NameError", "Variable '" + name + "' is not declared", currentLine));
        }

        return null;
    }


    @Override
    public Void visitAssignExpr(AssignExpr expr) {
        setLine(expr.name.line);
        expr.value.accept(this);

        String name = expr.name.lexeme;

        int slot = getLocalSlot(name);
        if (slot != -1) {
            emit(OpCode.STORE_VAR.getCode());
            emitShort(slot);
        } else if (globals.containsKey(name)) {
            int index = makeConstant(name); // Correctly add the string name to constant pool
            emit(OpCode.STORE_GLOBAL.getCode());
            emitShort(index);
        } else {
            errors.add(new KlyvosCompileError("NameError", "Cannot assign undeclared variable '" + name + "'", currentLine));
        }

        return null;
    }


    @Override
    public Void visitUnaryExpr(UnaryExpr expr) {
        setLine(expr.operator.line);
        expr.right.accept(this);
        switch (expr.operator.type) {
            case MINUS -> emit(OpCode.NEG.getCode());
            case NOT -> emit(OpCode.NOT.getCode());
            default ->
                    errors.add(new KlyvosCompileError("UnknownOperator", "Unknown Unary Operator: " + expr.operator.lexeme + "\n In " + currentName, currentLine));
        }
        return null;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr expr) {
        setLine(expr.operator.line);
        expr.left.accept(this);
        expr.right.accept(this);
        switch (expr.operator.type) {
            case PLUS -> emit(OpCode.ADD.getCode());
            case MINUS -> emit(OpCode.SUB.getCode());
            case STAR -> emit(OpCode.MUL.getCode());
            case SLASH -> emit(OpCode.DIV.getCode());
            case MOD -> emit(OpCode.MOD.getCode());
            case EQUALS -> emit(OpCode.EQ.getCode());
            case NEQ -> emit(OpCode.NEQ.getCode());
            case LT -> emit(OpCode.LT.getCode());
            case LE -> emit(OpCode.LTE.getCode());
            case GT -> emit(OpCode.GT.getCode());
            case GE -> emit(OpCode.GTE.getCode());
            case EXP -> emit(OpCode.EXP.getCode());
            case IN -> emit(OpCode.IN.getCode());
            default ->
                    errors.add(new KlyvosCompileError("UnknownOperator", "Unknown Binary Operator in " + currentName + ": " + expr.operator.lexeme, currentLine));
        }
        return null;
    }

    @Override
    public Void visitGroupingExpr(GroupingExpr expr) {
        setLine(expr.line());
        return expr.expression.accept(this);
    }

    @Override
    public Void visitLogicalExpr(LogicalExpr expr) {
        setLine(expr.operator.line);
        expr.left.accept(this);
        int shortCircuitJump;
        if (expr.operator.type == TokenType.OR) {
            shortCircuitJump = emitJump(OpCode.JUMP.getCode());
        } else if (expr.operator.type == TokenType.AND) {
            shortCircuitJump = emitJump(OpCode.JUMP_IF_FALSE.getCode());
        } else {
            errors.add(new KlyvosCompileError("Invalid logical operator", currentName, currentLine));
            return null;
        }
        emit(OpCode.POP.getCode());
        expr.right.accept(this);
        patchJump(shortCircuitJump);
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr expr) {
        setLine(expr.line());

        // Native method call: e.g. console.write(...)
        if (expr.calle instanceof GetExpr getExpr) {
            getExpr.expression.accept(this); // emit receiver (console)
            boolean isNativeModule = getExpr.expression instanceof VariableExpr var
                    && importedModules.getOrDefault(var.name.lexeme, ModuleRegistry.Type.SCRIPT)
                    == ModuleRegistry.Type.NATIVE;

            if (!isNativeModule) {
                // Arguments left-to-right
                for (Expression arg : expr.arguments) arg.accept(this);
                int nameIdx = makeConstant(getExpr.name.literal);
                emit(OpCode.CALL_METHOD.getCode());
                emitShort(nameIdx);
                emitShort(expr.arguments.size());
                return null;
            } else {
                // Native module call: e.g. console.write native
                String mod = ((VariableExpr) getExpr.expression).name.lexeme;
                String fn = getExpr.name.lexeme;
                for (Expression arg : expr.arguments) arg.accept(this);
                int mi = makeConstant(mod);
                int fi = makeConstant(fn);
                emit(OpCode.NATIVE_CALL.getCode());
                emitShort(mi);
                emitShort(fi);
                emitShort(expr.arguments.size());
                return null;
            }
        }

        // Normal function call: arguments, then callee, then CALL
        for (Expression arg : expr.arguments) arg.accept(this);
        expr.calle.accept(this);
        emit(OpCode.CALL.getCode());
        emitShort(expr.arguments.size());
        return null;
    }

    @Override
    public Void visitGetExpr(GetExpr expr) {
        expr.expression.accept(this);
        int nameIndex = makeConstant(expr.name.literal);
        emit(OpCode.GET_PROP.getCode());
        emitShort(nameIndex);
        return null;
    }

    @Override
    public Void visitIndexExpr(IndexExpr expr) {
        setLine(expr.line);
        expr.target.accept(this);
        boolean isSlice = expr.end != null || expr.step != null;
        if (isSlice) {
            if (expr.start != null) expr.start.accept(this);
            else {
                emit(OpCode.LOAD_CONST.getCode());
                emitShort(makeConstant(null));
            }
            if (expr.end != null) expr.end.accept(this);
            else {
                emit(OpCode.LOAD_CONST.getCode());
                emitShort(makeConstant(null));
            }
            if (expr.step != null) expr.step.accept(this);
            else {
                emit(OpCode.LOAD_CONST.getCode());
                emitShort(makeConstant(null));
            }
            emit(OpCode.SLICE.getCode());
        } else {
            expr.start.accept(this);
            emit(OpCode.GET_INDEX.getCode());
        }
        return null;
    }

    @Override
    public Void visitListExpr(ListExpr expr) {
        setLine(expr.line());
        for (Expression element : expr.elements) element.accept(this);
        emit(OpCode.MAKE_LIST.getCode());
        emit((byte) expr.elements.size());
        return null;
    }

    @Override
    public Void visitErrorExpr(ErrorExpr errorExpr) {
        errors.add(errorExpr.error);
        emit(OpCode.NOOP.getCode());
        return null;
    }

    @Override
    public Void visitErrorStmt(ErrorStmt stmt) {
        errors.add(stmt.error);
        emit(OpCode.NOOP.getCode());
        return null;
    }

    @Override
    public Void visitExpressionStmt(ExpressionStmt stmt) {
        setLine(stmt.expression.line());
        if (!(stmt.expression instanceof AssignExpr)) {
            stmt.expression.accept(this);
            emit(OpCode.POP.getCode());
        } else {
            stmt.expression.accept(this); // Don't pop
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(BlockStmt stmt) {
        enterScope(false);
        for (Statement statement : stmt.statements) statement.accept(this);
        exitScope();
        return null;
    }

    @Override
    public Void visitBreakStmt(BreakStmt stmt) {
        setLine(stmt.keyword.line);
        if (breakJumpStack.isEmpty()) {
            errors.add(new KlyvosCompileError("BreakOutsideLoop", "Break outside loop", currentLine));
            return null;
        }
        int loc = emitJump(OpCode.BREAK.getCode());
        breakJumpStack.peek().add(loc);
        return null;
    }

    @Override
    public Void visitContinueStmt(ContinueStmt stmt) {
        setLine(stmt.keyword.line);
        if (continueJumpStack.isEmpty()) {
            errors.add(new KlyvosCompileError("ContinueOutsideLoop", "Continue outside loop", currentLine));
            return null;
        }
        int loc = emitJump(OpCode.CONTINUE.getCode());
        continueJumpStack.peek().add(loc);
        return null;
    }


    @Override
    public Void visitIfStmt(IfStmt stmt) {
        setLine(stmt.ifToken.line);
        stmt.condition.accept(this);                    // condition
        int elseJump = emitJump(OpCode.JUMP_IF_FALSE.getCode());  // jump if false (and consumes condition)

        stmt.thenBranch.accept(this);
        int endJump = emitJump(OpCode.JUMP.getCode());
        patchJump(elseJump);

        if (stmt.elseBranch != null) {
            stmt.elseBranch.accept(this);
        }

        patchJump(endJump);
        return null;
    }


    @Override
    public Void visitWhileStmt(WhileStmt stmt) {
        setLine(stmt.keyword.line);

        List<Integer> breakJumps = new ArrayList<>();
        List<Integer> continueJumps = new ArrayList<>();

        int loopStart = chunk.size(); // Label for start of loop

        // Evaluate condition
        stmt.condition.accept(this);
        int exitJump = emitJump(OpCode.JUMP_IF_FALSE.getCode());

        // Save context
        this.breakJumpStack.push(breakJumps);
        this.continueJumpStack.push(continueJumps);

        // Loop body
        stmt.body.accept(this);

        // Patch all continue jumps to go back to loopStart
        for (int loc : continueJumps) patchJumpTo(loc, loopStart);

        // Jump back to start
        emit(OpCode.JUMP.getCode());
        emitShort(loopStart);

        // Patch exit condition
        patchJump(exitJump);

        // 🔴 Removed: emit(OpCode.POP.getCode());

        // Patch all breaks to jump here
        int loopExit = chunk.size();
        for (int loc : breakJumps) patchJumpTo(loc, loopExit);

        // Restore stacks
        breakJumpStack.pop();
        continueJumpStack.pop();

        return null;
    }


    @Override
    public Void visitReturnStmt(ReturnStmt stmt) {
        setLine(stmt.keyword.line);
        if (stmt.value != null) stmt.value.accept(this);
        else emitConstant(null);
        emit(OpCode.RETURN.getCode());
        return null;
    }

    @Override
    public Void visitVarDeclStmt(VarDeclStmt stmt) {
        setLine(stmt.keyword.line);
        if (stmt.initializer != null) stmt.initializer.accept(this);
        else emitConstant(null);
        int slot = declareLocal(stmt.name.lexeme);
        emit(OpCode.STORE_VAR.getCode());
        emitShort(slot);
        return null;
    }

    @Override
    public Void visitConstDeclStmt(ConstDeclStmt stmt) {
        setLine(stmt.name.line);
        stmt.value.accept(this);
        int slot = declareLocal(stmt.name.lexeme);
        emit(OpCode.STORE_VAR.getCode());
        emitShort(slot);
        return null;
    }

    @Override
    public Void visitFnDeclStmt(FnDeclStmt stmt) {
        setLine(stmt.name.line);
        Chunk fnChunk = new Chunk(stmt.name.lexeme);
        Compiler fnCompiler = new Compiler(fnChunk, errors, stmt.name.lexeme, modules, globals);

        // Enter one scope — this will hold both parameters and function body
        fnCompiler.enterScope(true);

        // Declare parameters
        for (FnDeclStmt.Parameter param : stmt.parameters) {
            fnCompiler.declareLocal(param.name.lexeme);
        }

        fnCompiler.enterScope(false);

        stmt.body.accept(fnCompiler);

        // Emit return if missing
        if (fnChunk.size() == 0 || fnChunk.getCode(fnChunk.size() - 1) != OpCode.RETURN.getCode()) {
            fnCompiler.emitConstant(null);
            fnCompiler.emit(OpCode.RETURN.getCode());
        }

        fnCompiler.exitScope();

        // Handle default values
        List<Chunk> defaultChunks = new ArrayList<>();
        for (FnDeclStmt.Parameter param : stmt.parameters) {
            if (param.defaultValue != null) {
                Chunk defaultChunk = fnCompiler.compileDefaultArg(
                        param.defaultValue, errors,
                        stmt.name.lexeme + ".default@" + param.name.lexeme
                );
                defaultChunks.add(defaultChunk);
            } else {
                defaultChunks.add(null);
            }
        }

        UserFunction fn = new UserFunction(
                stmt.name.lexeme,
                stmt.parameters.stream().map(p -> p.name.lexeme).toList(),
                defaultChunks,
                fnChunk
        );
        globals.put(stmt.name.literal.toString(), Value.NULL);
        emitConstant(Value.ofFunction(fn));
        int index = makeConstant(stmt.name.literal);
        emit(OpCode.STORE_GLOBAL.getCode());
        emitShort(index);
        return null;
    }


    public Chunk compileDefaultArg(Expression expr, List<KlyvosError> errors, String name) {
        Compiler compiler = new Compiler(new Chunk(name), errors, name, modules, globals);
        compiler.compile(expr);
        compiler.emit(OpCode.RETURN.getCode());
        return compiler.chunk;
    }

    @Override
    public Void visitUseStmt(UseStmt stmt) {
        setLine(stmt.moduleToken.line);
        String modName = stmt.moduleToken.literal.toString();


        // 1) Teach the compiler that 'modName' is now a declared global:
        globals.put(modName, Value.of(
                // placeholder namespace; VM will overwrite with real exports
                new ModuleNamespace(modName, Map.of())
        ));

        // Always emit USE_MODULE and STORE_GLOBAL
        int index = makeConstant(modName);
        emit(OpCode.USE_MODULE.getCode());
        emitShort(index);
        emit(OpCode.STORE_GLOBAL.getCode());
        emitShort(index);

        return null;
    }


    @Override
    public Void visitForStmt(ForStmt stmt) {
        setLine(stmt.keyword.line);

        List<Integer> breakJumps = new ArrayList<>();
        List<Integer> continueJumps = new ArrayList<>();

        // Evaluate iterable and store it in __klyvos_iter_<name>
        String iterableTemp = "__klyvos_iter_" + stmt.variable.lexeme;
        stmt.iterable.accept(this);
        int iterableSlot = declareLocal(iterableTemp);
        emit(OpCode.STORE_VAR.getCode());
        emitShort(iterableSlot);

        // Initialize index = 0 and store in __klyvos_index_<name>
        String indexTemp = "__klyvos_index_" + stmt.variable.lexeme;
        emitConstant(0);
        int indexSlot = declareLocal(indexTemp);
        emit(OpCode.STORE_VAR.getCode());
        emitShort(indexSlot);

        int loopStart = chunk.size(); // ⭕

        // CONDITION: index in iterable → reverse order for IN
        emit(OpCode.LOAD_VAR.getCode());
        emitShort(indexSlot);      // index
        emit(OpCode.LOAD_VAR.getCode());
        emitShort(iterableSlot);   // iterable
        emit(OpCode.IN.getCode());  // emits: index in iterable

        int exitJump = emitJump(OpCode.JUMP_IF_FALSE.getCode());

        // GET ELEMENT: iterable[index]
        emit(OpCode.LOAD_VAR.getCode());
        emitShort(iterableSlot);
        emit(OpCode.LOAD_VAR.getCode());
        emitShort(indexSlot);
        emit(OpCode.GET_INDEX.getCode());

        // Assign to loop variable
        int loopVarSlot = declareLocal(stmt.variable.lexeme);
        emit(OpCode.STORE_VAR.getCode());
        emitShort(loopVarSlot);

        // Push jump targets for break/continue
        breakJumpStack.push(breakJumps);
        continueJumpStack.push(continueJumps);

        enterScope(false);
        stmt.body.accept(this);
        exitScope();

        // CONTINUE patch target: index += 1
        int continueTarget = chunk.size();
        for (int loc : continueJumpStack.peek()) patchJumpTo(loc, continueTarget);

        emit(OpCode.LOAD_VAR.getCode());
        emitShort(indexSlot);
        emitConstant(1);
        emit(OpCode.ADD.getCode());
        emit(OpCode.STORE_VAR.getCode());
        emitShort(indexSlot);

        emit(OpCode.JUMP.getCode());
        emitShort(loopStart);

        int loopExit = chunk.size();
        patchJump(exitJump);

        for (int loc : breakJumpStack.peek()) patchJumpTo(loc, loopExit);

        breakJumpStack.pop();
        continueJumpStack.pop();

        return null;
    }


    private void patchJumpTo(int jumpPos, int target) {
        int offset = target;
        chunk.getCode().set(jumpPos, (byte) ((offset >> 8) & 0xff));
        chunk.getCode().set(jumpPos + 1, (byte) (offset & 0xff));
    }

}