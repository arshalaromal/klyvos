package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.ast.Statement;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class VarDeclStmt implements Statement {
    public final Token name;
    public final Token keyword;
    public final Expression initializer;

    public VarDeclStmt(Token keyword, Token name, Expression initializer) {
        this.name = name;
        this.keyword = keyword;
        this.initializer = initializer;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVarDeclStmt(this);
    }
}
