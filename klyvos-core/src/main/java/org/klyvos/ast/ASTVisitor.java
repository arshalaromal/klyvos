package org.klyvos.ast;

import org.klyvos.ast.expressions.*;
import org.klyvos.ast.statements.*;
import org.klyvos.error.ErrorExpr;
import org.klyvos.error.ErrorStmt;

/**
 * @author Arshal Aromal
 */
public interface ASTVisitor<T> {

    T visitProgram(Program program);

    T visitUseStmt(UseStmt stmt);

    T visitVarDeclStmt(VarDeclStmt stmt);

    T visitConstDeclStmt(ConstDeclStmt stmt);

    T visitFnDeclStmt(FnDeclStmt stmt);

    T visitIfStmt(IfStmt stmt);

    T visitWhileStmt(WhileStmt stmt);

    T visitForStmt(ForStmt stmt);

    T visitBreakStmt(BreakStmt stmt);

    T visitContinueStmt(ContinueStmt stmt);

    T visitReturnStmt(ReturnStmt stmt);

    T visitBlockStmt(BlockStmt stmt);

    T visitExpressionStmt(ExpressionStmt stmt);

    T visitBinaryExpr(BinaryExpr expr);

    T visitUnaryExpr(UnaryExpr expr);

    T visitGetExpr(GetExpr expr);

    T visitGroupingExpr(GroupingExpr expr);

    T visitLiteralExpr(LiteralExpr expr);

    T visitVariableExpr(VariableExpr expr);

    T visitAssignExpr(AssignExpr expr);

    T visitLogicalExpr(LogicalExpr expr);

    T visitCallExpr(CallExpr expr);

    T visitIndexExpr(IndexExpr expr);

    T visitListExpr(ListExpr expr);

    T visitErrorStmt(ErrorStmt stmt);

    T visitErrorExpr(ErrorExpr errorExpr);
}