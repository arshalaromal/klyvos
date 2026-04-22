package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Statement;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class ContinueStmt implements Statement {
    public final Token keyword;

    public ContinueStmt(Token keyword) {
        this.keyword = keyword;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitContinueStmt(this);
    }
}
