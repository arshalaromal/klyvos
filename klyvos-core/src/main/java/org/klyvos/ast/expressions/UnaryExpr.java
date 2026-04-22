package org.klyvos.ast.expressions;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class UnaryExpr implements Expression {
    public final Token operator;
    public final Expression right;

    public UnaryExpr(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }


    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitUnaryExpr(this);
    }

    @Override
    public int line() {
        return operator.line;
    }
}
