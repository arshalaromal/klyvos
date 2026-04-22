package org.klyvos.error;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Statement;

/**
 * @author Arshal Aromal
 */
public class ErrorStmt implements Statement {
    public final KlyvosError error;

    public ErrorStmt(KlyvosError error) {
        this.error = error;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitErrorStmt(this);
    }
}
