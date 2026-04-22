package org.klyvos.ast.expressions;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class GetExpr implements Expression {
    public final Expression expression;
    public final Token name;

    public GetExpr(Expression expression, Token name) {
        this.expression = expression;
        this.name = name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitGetExpr(this);
    }

    @Override
    public int line() {
        return name.line;
    }
}
