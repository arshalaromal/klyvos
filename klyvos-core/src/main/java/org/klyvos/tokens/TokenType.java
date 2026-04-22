package org.klyvos.tokens;

/**
 * @author Arshal Aromal
 */
public enum TokenType {
    EOF,

    //Keywords

    USE, VAR,
    CONST,
    FN, IF,
    ELSE, WHILE,
    FOR, IN,
    AND, OR,
    NOT, BREAK,
    CONTINUE, RETURN,

    //Literals
    IDENTIFIER, STRING, INTEGER, DECIMAL, BOOLEAN, NULL,

    //Single Character
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,
    COLON,
    SCOLON,
    COMMA,
    DOT,
    PLUS,
    MINUS,
    STAR,
    SLASH,
    MOD,
    EXP,
    EQUALS,
    ASSIGN,
    NEQ,
    LE,
    GE,
    LT,
    GT,

}