package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.ast.Statement;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class WhileStmt implements Statement {
    public final Expression condition;
    public final BlockStmt body;
    public final Token keyword;

    public WhileStmt(Token keyword, Expression condition, BlockStmt body) {
        this.condition = condition;
        this.body = body;
        this.keyword = keyword;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitWhileStmt(this);
    }
}
