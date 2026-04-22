package org.klyvos.ast.expressions;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;

import java.util.List;

/**
 * @author Arshal Aromal
 */
public class ListExpr implements Expression {
    public final List<Expression> elements;
    public final int line;

    public ListExpr(List<Expression> elements, int line) {
        this.elements = elements;
        this.line = line;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitListExpr(this);
    }

    @Override
    public int line() {
        return line;
    }
}
