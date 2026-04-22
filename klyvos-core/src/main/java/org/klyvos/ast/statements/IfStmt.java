package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.ast.Statement;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class IfStmt implements Statement {
    public final Expression condition;
    public final BlockStmt thenBranch;
    public final Statement elseBranch;
    public final Token ifToken;

    public IfStmt(Token ifToken, Expression condition, BlockStmt thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.ifToken = ifToken;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIfStmt(this);
    }
}
