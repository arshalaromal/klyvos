package org.klyvos.ast;

public interface ASTNode {
    <T> T accept(ASTVisitor<T> visitor);
}