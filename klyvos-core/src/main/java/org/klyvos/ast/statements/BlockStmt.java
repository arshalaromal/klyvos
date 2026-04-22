package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Statement;

import java.util.List;

/**
 * @author Arshal Aromal
 */
public class BlockStmt implements Statement {
    public final List<Statement> statements;

    public BlockStmt(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBlockStmt(this);
    }
}
