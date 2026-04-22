package org.klyvos.ast.expressions;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;

import java.util.List;

/**
 * @author Arshal Aromal
 */
public class CallExpr implements Expression {
    public final Expression calle;
    public final List<Expression> arguments;
    public final int line;

    public CallExpr(Expression calle, List<Expression> arguments, int line) {

        this.calle = calle;
        this.arguments = arguments;
        this.line = line;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitCallExpr(this);
    }

    @Override
    public int line() {
        return line;
    }
}