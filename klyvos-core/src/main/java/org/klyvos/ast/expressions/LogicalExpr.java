package org.klyvos.ast.expressions;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class LogicalExpr implements Expression {
    public final Expression left;
    public final Token operator;
    public final Expression right;

    public LogicalExpr(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLogicalExpr(this);
    }

    @Override
    public int line() {
        return operator.line;
    }
}
