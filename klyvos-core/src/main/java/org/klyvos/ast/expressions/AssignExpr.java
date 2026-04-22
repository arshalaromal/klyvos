package org.klyvos.ast.expressions;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class AssignExpr implements Expression {
    public final Token name;
    public final Expression value;

    public AssignExpr(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignExpr(this);
    }

    @Override
    public int line() {
        return name.line;
    }
}
