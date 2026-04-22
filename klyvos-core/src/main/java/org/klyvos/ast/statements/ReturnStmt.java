package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.ast.Statement;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class ReturnStmt implements Statement {
    public final Token keyword;
    public final Expression value;

    public ReturnStmt(Token keyword, Expression value) {
        this.keyword = keyword;
        this.value = value;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitReturnStmt(this);
    }
}
