package org.klyvos.error;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;

public class ErrorExpr implements Expression {
    public final KlyvosError error;

    public ErrorExpr(KlyvosError error) {
        this.error = error;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitErrorExpr(this);
    }

    @Override
    public int line() {
        return error.getLine();
    }
}
