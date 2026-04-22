package org.klyvos.ast.expressions;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class VariableExpr implements Expression {
    public final Token name;

    public VariableExpr(Token name) {
        this.name = name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVariableExpr(this);
    }

    @Override
    public int line() {
        return name.line;
    }
}
