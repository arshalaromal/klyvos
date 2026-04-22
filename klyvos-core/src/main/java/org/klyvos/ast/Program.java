package org.klyvos.ast;

import java.util.List;

/**
 * @author Arshal Aromal
 */
public class Program implements ASTNode {
    public final List<Statement> statements;

    public Program(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitProgram(this);
    }
}
