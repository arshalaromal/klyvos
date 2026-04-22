package org.klyvos.ast.printer;

import org.klyvos.ast.ASTNode;
import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Program;
import org.klyvos.ast.Statement;
import org.klyvos.ast.expressions.*;
import org.klyvos.ast.statements.*;
import org.klyvos.error.ErrorExpr;
import org.klyvos.error.ErrorStmt;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arshal Aromal
 */
public class PrettyPrinter implements ASTVisitor<String> {


    public String print(ASTNode node) {
        return node == null ? "null" : node.accept(this);

    }


    private String join(List<? extends ASTNode> nodes, String sep) {
        return nodes.stream().map(
                n -> n.accept(this)).collect(Collectors.joining(sep)
        );
    }


    @Override
    public String visitProgram(Program program) {
        StringBuilder sb = new StringBuilder();
        for (Statement stmt : program.statements) {
            sb.append(stmt.accept(this)).append("\n");
        }

        return sb.toString().trim(); // removes the last newline
    }

    @Override
    public String visitUseStmt(UseStmt stmt) {
        return "use " + stmt.moduleToken.lexeme;
    }

    @Override
    public String visitVarDeclStmt(VarDeclStmt stmt) {
        String init = stmt.initializer != null ? " = " + stmt.initializer.accept(this) : "";
        return "var " + stmt.name.lexeme + init;
    }

    @Override
    public String visitConstDeclStmt(ConstDeclStmt stmt) {
        return "const " + stmt.name.lexeme + " = " + print(stmt.value);
    }

    @Override
    public String visitFnDeclStmt(FnDeclStmt stmt) {
        String params = stmt.parameters.stream()
                .map(p -> p.name.lexeme)
                .collect(Collectors.joining(", "));
        return "fn " + stmt.name.lexeme + "(" + params + ") " + stmt.body.accept(this);
    }

    @Override
    public String visitIfStmt(IfStmt stmt) {
        String code = "if (" + stmt.condition.accept(this) + ") " + stmt.thenBranch.accept(this);
        if (stmt.elseBranch != null) {
            code += " else " + stmt.elseBranch.accept(this);
        }
        return code;
    }

    @Override
    public String visitWhileStmt(WhileStmt stmt) {
        return "while (" + print(stmt.condition) + ") " + print(stmt.body);
    }

    @Override
    public String visitForStmt(ForStmt stmt) {
        return "for (" + stmt.variable.lexeme + " in " + print(stmt.iterable) + ") " + print(stmt.body);
    }

    @Override
    public String visitBreakStmt(BreakStmt stmt) {
        return "break";
    }

    @Override
    public String visitContinueStmt(ContinueStmt stmt) {
        return "continue";
    }

    @Override
    public String visitReturnStmt(ReturnStmt stmt) {
        if (stmt.value != null) {
            return "return " + print(stmt.value);
        }
        return "return";
    }

    @Override
    public String visitBlockStmt(BlockStmt stmt) {
        return "{\n" + join(stmt.statements, "\n") + "\n}";
    }

    @Override
    public String visitExpressionStmt(ExpressionStmt stmt) {
        return print(stmt.expression);
    }

    @Override
    public String visitBinaryExpr(BinaryExpr expr) {
        return "(" + expr.left.accept(this) + " "
                + expr.operator.lexeme + " " + expr.right.accept(this) + ")";
    }

    @Override
    public String visitUnaryExpr(UnaryExpr expr) {
        return expr.operator.lexeme + print(expr.right);
    }

    @Override
    public String visitGetExpr(GetExpr expr) {
        return print(expr.expression) + "." + expr.name.lexeme;
    }

    @Override
    public String visitGroupingExpr(GroupingExpr expr) {
        return "(" + print(expr.expression) + ")";
    }

    @Override
    public String visitLiteralExpr(LiteralExpr expr) {
        return expr.value.lexeme;
    }

    @Override
    public String visitVariableExpr(VariableExpr expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitAssignExpr(AssignExpr expr) {
        return expr.name.lexeme + " = " + print(expr.value);
    }

    @Override
    public String visitLogicalExpr(LogicalExpr expr) {
        return print(expr.left) + " " + expr.operator.lexeme + " " + print(expr.right);
    }

    @Override
    public String visitCallExpr(CallExpr expr) {

        String args = expr.arguments.stream()
                .map(this::print)
                .collect(Collectors.joining(", "));
        return print(expr.calle) + "(" + args + ")";

    }

    @Override
    public String visitIndexExpr(IndexExpr expr) {
        StringBuilder slice = new StringBuilder();
        if (expr.start != null) slice.append(print(expr.start));
        slice.append(":");
        if (expr.end != null) slice.append(print(expr.end));
        if (expr.step != null) slice.append(":").append(print(expr.step));
        return print(expr.target) + "[" + slice + "]";
    }

    @Override
    public String visitListExpr(ListExpr expr) {
        String elements = expr.elements.stream()
                .map(this::print)
                .collect(Collectors.joining(", "));
        return "[" + elements + "]";
    }

    @Override
    public String visitErrorStmt(ErrorStmt stmt) {
        return stmt.error.format();
    }

    @Override
    public String visitErrorExpr(ErrorExpr errorExpr) {
        return errorExpr.error.format();
    }


}
