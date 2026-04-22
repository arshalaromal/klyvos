package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.ast.Statement;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class ForStmt implements Statement {
    public final Token variable;
    public final Expression iterable;
    public final BlockStmt body;
    public final Token keyword;

    public ForStmt(Token keyword, Token variable, Expression iterable, BlockStmt body) {
        this.variable = variable;
        this.iterable = iterable;
        this.body = body;
        this.keyword = keyword;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitForStmt(this);
    }
}
