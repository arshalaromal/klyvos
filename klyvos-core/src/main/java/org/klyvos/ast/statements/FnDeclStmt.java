package org.klyvos.ast.statements;

import org.klyvos.ast.ASTVisitor;
import org.klyvos.ast.Expression;
import org.klyvos.ast.Statement;
import org.klyvos.tokens.Token;

import java.util.List;

/**
 * @author Arshal Aromal
 */
public class FnDeclStmt implements Statement {

    public final Token name;
    public final List<Parameter> parameters;
    public final BlockStmt body;
    public final Token keyword;

    public FnDeclStmt(Token keyword, Token name, List<Parameter> parameters, BlockStmt body) {

        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.keyword = keyword;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFnDeclStmt(this);
    }

    public static class Parameter {
        public final Token name;
        public final Expression defaultValue;

        public Parameter(Token name, Expression defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }
    }

}