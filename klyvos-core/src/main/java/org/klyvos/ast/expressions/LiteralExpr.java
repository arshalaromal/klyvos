package org.klyvos.ast.expressions;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.tokens.Token;
import org.klyvos.vm.ValueType;

/**
 * @author Arshal Aromal
 */
public class LiteralExpr implements Expression {
    public final Token value;
    public final ValueType type;

    public LiteralExpr(Token value, ValueType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLiteralExpr(this);
    }

    @Override
    public int line() {
        return value.line;
    }
}
