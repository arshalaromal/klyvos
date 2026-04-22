package org.klyvos.ast.expressions;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;

/**
 * @author Arshal Aromal
 */
public class GroupingExpr implements Expression {
    public final Expression expression;
    public final int line;

    public GroupingExpr(Expression expression, int line) {
        this.expression = expression;
        this.line = line;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitGroupingExpr(this);
    }

    @Override
    public int line() {
        return line;
    }
}
