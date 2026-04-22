package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.ast.Statement;

/**
 * @author Arshal Aromal
 */
public class ExpressionStmt implements Statement {
    public final Expression expression;

    public ExpressionStmt(Expression expression) {
        this.expression = expression;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitExpressionStmt(this);
    }
}
