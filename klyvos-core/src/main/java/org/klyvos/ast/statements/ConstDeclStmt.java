package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.ast.Statement;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class ConstDeclStmt implements Statement {
    public final Token name;
    public final Expression value;
    public final Token keyword;

    public ConstDeclStmt(Token keyword, Token name, Expression value) {
        this.name = name;
        this.value = value;
        this.keyword = keyword;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitConstDeclStmt(this);
    }
}
