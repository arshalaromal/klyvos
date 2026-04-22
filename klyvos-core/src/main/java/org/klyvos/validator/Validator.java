package org.klyvos.validator;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.ast.Program;
import org.klyvos.ast.Statement;
import org.klyvos.ast.expressions.*;
import org.klyvos.ast.statements.*;
import org.klyvos.error.ErrorExpr;
import org.klyvos.error.ErrorReporter;
import org.klyvos.error.ErrorStmt;
import org.klyvos.error.KlyvosCompileError;
import org.klyvos.tokens.Token;
import org.klyvos.tokens.TokenType;

import java.util.*;

/**
 * @author Arshal Aromal
 */

public class Validator implements ASTVisitor<Void> {
    private final ErrorReporter reporter;
    private final Deque<Scope> scopes = new ArrayDeque<>();
    private boolean inFunction = false;
    private boolean inLoop = false;

    public Validator(ErrorReporter reporter) {
        this.reporter = reporter;
        beginScope();
    }

    private void beginScope() {
        scopes.push(new Scope());
    }

    private void endScope() {
        scopes.pop();
    }

    private Scope currentScope() {
        return scopes.peek();
    }


    //Handle Break Outside Loop
    @Override
    public Void visitBreakStmt(BreakStmt stmt) {
        if (!inLoop) {
            reporter.report(new KlyvosCompileError(
                    "LoopControlError",
                    "`break` used outside of a loop",
                    stmt.keyword.line
            ));
        }
        return null;
    }

    //Handle Continue Outside Loop
    @Override
    public Void visitContinueStmt(ContinueStmt stmt) {
        if (!inLoop) {
            reporter.report(new KlyvosCompileError(
                    "LoopControlError",
                    "`continue` used outside of a loop",
                    stmt.keyword.line
            ));
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt stmt) {

        stmt.condition.accept(this);

        if (isDefinitelyNotBoolean(stmt.condition)) {
            String badVal = "<non-literal>";
            if (stmt.condition instanceof LiteralExpr literal) {
                badVal = literal.value == null ? "null" : literal.value.toString();
            }

            reporter.report(new KlyvosCompileError(
                    "TypeError",
                    "Condition in 'while' must be a boolean (got: " + badVal + ")",
                    stmt.keyword.line
            ));
        }

        boolean prev = inLoop;
        inLoop = true;
        stmt.body.accept(this);
        inLoop = prev;
        return null;
    }

    @Override
    public Void visitForStmt(ForStmt stmt) {
        stmt.iterable.accept(this);
        // Statically validate only known cases: string literals
        if (stmt.iterable instanceof LiteralExpr lit) {
            if (!((
                    lit.value.literal instanceof String))) {
                reporter.report(new KlyvosCompileError(
                        "TypeError",
                        "For-loop can only iterate over lists or strings",
                        stmt.keyword.line
                ));
            }
        }

        boolean prevInLoop = inLoop;
        inLoop = true;

        beginScope();
        currentScope().declare(stmt.variable.lexeme, false);
        stmt.body.accept(this);
        endScope();

        inLoop = prevInLoop;

        return null;
    }

    @Override
    public Void visitVariableExpr(VariableExpr expr) {
        String name = expr.name.lexeme;
        if (!isVariableVisible(name)) {
            reporter.report(new KlyvosCompileError(
                    "NameError",
                    "Variable '" + name + "' is not declared",
                    expr.name.line
            ));
        }
        return null;
    }

    @Override
    public Void visitVarDeclStmt(VarDeclStmt stmt) {
        String name = stmt.name.lexeme;

        if (!(currentScope().declare(name, false))) {
            reporter.report(new KlyvosCompileError(
                    "DuplicateDeclarationError",
                    "Variable '" + name + "' is already declared in this scope",
                    stmt.name.line
            ));
        }

        if (stmt.initializer != null) {
            stmt.initializer.accept(this);
        }
        return null;
    }

    @Override
    public Void visitConstDeclStmt(ConstDeclStmt stmt) {
        String name = stmt.name.lexeme;

        if (!currentScope().declare(name, true)) {
            reporter.report(new KlyvosCompileError(
                    "DuplicateDeclarationError",
                    "Constant '" + name + "' is already declared in this scope",
                    stmt.name.line
            ));
        }

        if (stmt.value != null) {
            stmt.value.accept(this);
        }
        return null;
    }

    @Override
    public Void visitAssignExpr(AssignExpr expr) {
        String name = expr.name.lexeme;

        // Check: is variable declared?
        if (!isVariableVisible(name)) {
            reporter.report(new KlyvosCompileError(
                    "NameError",
                    "Variable '" + name + "' is not declared",
                    expr.name.line
            ));
        } else {
            // Check: is variable const?
            for (Iterator<Scope> it = scopes.descendingIterator(); it.hasNext(); ) {
                Scope scope = it.next();
                if (scope.isDeclared(name)) {
                    if (scope.isConst(name)) {
                        reporter.report(new KlyvosCompileError(
                                "ConstAssignError",
                                "Cannot reassign to constant '" + name + "'",
                                expr.name.line
                        ));
                    }
                    break; // stop once we find the declaration
                }
            }

        }

        // Also validate the expression on the right side
        expr.value.accept(this);
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt stmt) {
        stmt.condition.accept(this);

        if (isDefinitelyNotBoolean(stmt.condition)) {

            String badVal = "<non-literal>";

            if (stmt.condition instanceof LiteralExpr literal) {
                if (literal.value == null) badVal = "null";
                else badVal = literal.value.toString();
            }

            reporter.report(new KlyvosCompileError(
                    "TypeError",
                    "Condition must be a boolean (got: " + badVal + ")",
                    stmt.ifToken.line
            ));
        }

        stmt.thenBranch.accept(this);
        if (stmt.elseBranch != null) {
            stmt.elseBranch.accept(this);
        }

        return null;
    }

    @Override
    public Void visitIndexExpr(IndexExpr expr) {
        expr.target.accept(this);

        if (expr.start != null) expr.start.accept(this);
        if (expr.end != null) expr.end.accept(this);
        if (expr.step != null) expr.step.accept(this);

        boolean isSlice = expr.end != null || expr.step != null;


        // Indexing mode
        if (!isSlice && expr.start instanceof LiteralExpr startLit) {
            if ((startLit.value.literal instanceof Token t)) {

                if (t.type != TokenType.INTEGER) {
                    reporter.report(new KlyvosCompileError(
                            "TypeError",
                            "Index must be an integer (got: " + startLit.value + ")",
                            startLit.value.line
                    ));
                }
            }
        }


        // Slicing mode (e.g., list[1:3:1])
        if (isSlice) {
            if (expr.start instanceof LiteralExpr s && !(s.value.literal instanceof Long)) {
                reporter.report(new KlyvosCompileError(
                        "TypeError",
                        "Slice start must be an integer (got: " + s.value + ")",
                        s.value.line
                ));
            }
            if (expr.end instanceof LiteralExpr e && !(e.value.literal instanceof Long)) {
                reporter.report(new KlyvosCompileError(
                        "TypeError",
                        "Slice end must be an integer (got: " + e.value + ")",
                        e.value.line
                ));
            }
            if (expr.step instanceof LiteralExpr st && !(st.value.literal instanceof Long)) {
                reporter.report(new KlyvosCompileError(
                        "TypeError",
                        "Slice step must be an integer (got: " + st.value + ")",
                        st.value.line
                ));
            }
        }

        // ──────────────────────────────
        // Validate the indexing target is valid (must be a list or string literal if known)
        if (expr.target instanceof LiteralExpr targetLit) {
            Object val = targetLit.value;
            boolean valid = val instanceof String || val instanceof List;
            if (!valid) {
                reporter.report(new KlyvosCompileError(
                        "TypeError",
                        "Only lists and strings can be indexed or sliced (got: " + val + ")",
                        targetLit.value.line
                ));
            }
        }

        return null;
    }

    @Override
    public Void visitBinaryExpr(BinaryExpr expr) {
        expr.left.accept(this);
        expr.right.accept(this);

        // Only validate if both sides are literal (i.e., known at compile time)
        if (expr.left instanceof LiteralExpr leftLit && expr.right instanceof LiteralExpr rightLit) {
            Object a = leftLit.value.literal;
            Object b = rightLit.value.literal;
            TokenType op = expr.operator.type;

            if (isArithmetic(op)) {
                if (!(a instanceof Long || a instanceof Double) || !(b instanceof Long || b instanceof Double)) {
                    reporter.report(new KlyvosCompileError(
                            "TypeError",
                            "Arithmetic operators require numbers (got: " + a + " and " + b + ")",
                            expr.operator.line
                    ));
                }
            }

            // Comparisons: <, <=, >, >=
            else if (isComparison(op)) {
                if (!sameComparableType(a, b)) {
                    reporter.report(new KlyvosCompileError(
                            "TypeError",
                            "Comparison requires same comparable types (got: " + a + " and " + b + ")",
                            expr.operator.line
                    ));
                }
            }

            // Membership: a in b
            else if (op == TokenType.IN) {
                boolean valid = (b instanceof List && ((List<?>) b).contains(a)) ||
                        (b instanceof String && a instanceof String);
                if (!valid) {
                    reporter.report(new KlyvosCompileError(
                            "TypeError",
                            "'in' operator requires element in list or string (got: " + a + " in " + b + ")",
                            expr.operator.line
                    ));
                }
            }

        }


        return null;
    }

    @Override
    public Void visitLogicalExpr(LogicalExpr expr) {
        expr.left.accept(this);
        expr.right.accept(this);

        if (expr.left instanceof LiteralExpr l && !(l.value.literal instanceof Boolean)) {
            reporter.report(new KlyvosCompileError(
                    "TypeError",
                    "Logical operator 'and/or' requires booleans (got: " + l.value + ")",
                    l.value.line
            ));
        }

        if (expr.right instanceof LiteralExpr r && !(r.value.literal instanceof Boolean)) {
            reporter.report(new KlyvosCompileError(
                    "TypeError",
                    "Logical operator 'and/or' requires booleans (got: " + r.value + ")",
                    r.value.line
            ));
        }

        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr expr) {
        expr.right.accept(this);

        if (expr.right instanceof LiteralExpr lit) {
            Object val = lit.value.literal;

            // "not" must operate on booleans
            if (expr.operator.type == TokenType.NOT && !(val instanceof Boolean)) {
                reporter.report(new KlyvosCompileError(
                        "TypeError",
                        "'not' operator requires a boolean (got: " + val + ")",
                        expr.operator.line
                ));
            }

            // "-" and "+" must operate on numbers
            if ((expr.operator.type == TokenType.MINUS)
                    && !(val instanceof Long || val instanceof Double)) {
                reporter.report(new KlyvosCompileError(
                        "TypeError",
                        "'" + expr.operator.lexeme + "' operator requires a number (got: " + val + ")",
                        expr.operator.line
                ));
            }
        }

        return null;
    }


    @Override
    public Void visitCallExpr(CallExpr expr) {
        expr.calle.accept(this);
        for (Expression arg : expr.arguments) {
            arg.accept(this); // Recursively validate each argument
        }
        // If the callee is not a valid callable expression type, warn
        if (expr.calle instanceof LiteralExpr lit) {
            Object literal = lit.value.literal;
            if (!(literal instanceof String)) {
                reporter.report(new KlyvosCompileError(
                        "TypeError",
                        "Only functions can be called. Got: " + literal,
                        lit.value.line
                ));
            }
        }

        return null;
    }

    @Override
    public Void visitFnDeclStmt(FnDeclStmt stmt) {


        if (inFunction) {
            reporter.report(new KlyvosCompileError("Error",
                    "Function Defined Inside Function", stmt.keyword.line));
        }

        String name = stmt.name.lexeme;
        if (!currentScope().declare(name, true)) { // true = const
            reporter.report(new KlyvosCompileError(
                    "FunctionRedefinition",
                    "Function '" + name + "' is already declared in this scope.",
                    stmt.keyword.line
            ));
        }
        beginScope();


        boolean enclosingFunction = inFunction;
        inFunction = true;

        // Declare parameters in function scope
        for (FnDeclStmt.Parameter param : stmt.parameters) {
            if (!currentScope().declare(param.name.lexeme, false)) {
                reporter.report(new KlyvosCompileError(
                        "DuplicateParameter",
                        "Parameter '" + param.name.lexeme +
                                "' is already declared in this function.",
                        stmt.keyword.line
                ));
            }
            if (param.defaultValue != null) {
                param.defaultValue.accept(this); // Validate default expression
            }
        }

        // Validate function body
        stmt.body.accept(this);

        //Restore previous state
        endScope();
        inFunction = enclosingFunction;
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt stmt) {
        if (!inFunction) {
            reporter.report(new KlyvosCompileError(
                    "ReturnOutsideFunction",
                    "Cannot return from top-level code.",
                    stmt.keyword.line));
        }

        if (stmt.value != null) {
            stmt.value.accept(this); // Validate the return expression
        }
        return null;
    }

    @Override
    public Void visitErrorExpr(ErrorExpr errorExpr) {
        reporter.report(errorExpr.error);
        return null;
    }

    @Override
    public Void visitErrorStmt(ErrorStmt stmt) {
        reporter.report(stmt.error);
        return null;
    }

    @Override
    public Void visitExpressionStmt(ExpressionStmt stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitGroupingExpr(GroupingExpr expr) {
        expr.expression.accept(this);
        return null;
    }

    @Override
    public Void visitLiteralExpr(LiteralExpr expr) {
        //No check for now
        return null;
    }

    @Override
    public Void visitListExpr(ListExpr expr) {
        for (Expression element : expr.elements) {
            element.accept(this); // validate each item
        }
        return null;
    }

    @Override
    public Void visitUseStmt(UseStmt stmt) {
        String moduleName = extractModuleName(stmt.moduleToken.lexeme);

        // Define it as a variable in global scope
        Scope globalScope = scopes.getLast(); // bottom-most scope is global
        globalScope.declare(moduleName, true); // treat imported modules as consts
        return null;
    }

    private String extractModuleName(String rawStringLiteral) {
        if (rawStringLiteral.startsWith("\"") && rawStringLiteral.endsWith("\"")) {
            return rawStringLiteral.substring(1, rawStringLiteral.length() - 1);
        }
        return rawStringLiteral;
    }

    @Override
    public Void visitBlockStmt(BlockStmt stmt) {
        beginScope();

        for (Statement statement : stmt.statements) {
            statement.accept(this); // validates each statement inside block
        }

        endScope();
        return null;
    }

    @Override
    public Void visitProgram(Program program) {
        for (Statement stmt : program.statements) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visitGetExpr(GetExpr expr) {
        // Validate the object expression which is trying to access a property from
        expr.expression.accept(this);

        // If the object is a literal (like string, number, null), accessing a property is not allowed
        if (expr.expression instanceof LiteralExpr lit) {
            Object value = lit.value.literal;

            reporter.report(new KlyvosCompileError(
                    "TypeError",
                    "Cannot access property '" + expr.name.lexeme + "' on primitive value: " + value,
                    expr.name.line
            ));
        }

        return null;
    }


    //Helper Functions
    private boolean isVariableVisible(String name) {
        for (Scope scope : scopes) {
            if (scope.isDeclared(name)) return true;
        }
        return false;
    }

    private boolean isDefinitelyNotBoolean(Expression expr) {
        if (expr instanceof LiteralExpr literal) {
            return !(literal.value.literal instanceof Boolean);
        }
        return false; // dynamic expression → assume valid
    }

    private boolean isArithmetic(TokenType type) {
        return switch (type) {
            case PLUS, MINUS, STAR, SLASH, MOD -> true;
            default -> false;
        };
    }

    private boolean isComparison(TokenType type) {
        return switch (type) {
            case LT, LE, GT, GE -> true;
            default -> false;
        };
    }

    private boolean sameComparableType(Object a, Object b) {
        return (a instanceof Long && b instanceof Long) ||
                (a instanceof Double && b instanceof Double) ||
                (a instanceof String && b instanceof String);
    }

    private static class Scope {
        final Map<String, Boolean> variables = new HashMap<>();   // var name → isConst

        boolean declare(String name, boolean isConst) {
            if (variables.containsKey(name)) return false;
            variables.put(name, isConst);
            return true;
        }

        boolean isDeclared(String name) {
            return variables.containsKey(name);
        }


        boolean isConst(String name) {
            return variables.getOrDefault(name, false);
        }

    }
}
