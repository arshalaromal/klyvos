package org.klyvos.ast.expressions;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;

/**
 * @author Arshal Aromal
 */
public class IndexExpr implements Expression {
    public final Expression target, start, end, step;
    public final int line;

    public IndexExpr(Expression target, Expression start, Expression end, Expression step, int line) {
        this.target = target;
        this.start = start;
        this.end = end;
        this.step = step;
        this.line = line;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIndexExpr(this);
    }

    @Override
    public int line() {
        return line;
    }
}
