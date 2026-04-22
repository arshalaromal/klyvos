package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Statement;
import org.klyvos.tokens.Token;

/**
 * @author Arshal Aromal
 */
public class UseStmt implements Statement {
    public final Token keyword;
    public final Token moduleToken;

    public UseStmt(Token keyword, Token moduleName) {
        this.keyword = keyword;
        this.moduleToken = moduleName;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitUseStmt(this);
    }
}
