package org.klyvos.tokens;

/**
 * @author Arshal Aromal
 */
public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int line;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;

    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                ", literal=" + literal +
                ", line=" + line +
                '}';
    }

    public static TokenType toTokenType(String tokenText) {
        return switch (tokenText) {
            case "use" -> TokenType.USE;
            case "var" -> TokenType.VAR;
            case "const" -> TokenType.CONST;
            case "fn" -> TokenType.FN;

            case "if" -> TokenType.IF;

            case "else" -> TokenType.ELSE;

            case "while" -> TokenType.WHILE;

            case "for" -> TokenType.FOR;

            case "in" -> TokenType.IN;

            case "and" -> TokenType.AND;

            case "or" -> TokenType.OR;

            case "not" -> TokenType.NOT;

            case "break" -> TokenType.BREAK;

            case "continue" -> TokenType.CONTINUE;

            case "return" -> TokenType.RETURN;

            case "true", "false" -> TokenType.BOOLEAN;
            case "null" -> TokenType.NULL;
            case "(" -> TokenType.LPAREN;

            case ")" -> TokenType.RPAREN;

            case "{" -> TokenType.LBRACE;

            case "}" -> TokenType.RBRACE;

            case "[" -> TokenType.LBRACK;

            case "]" -> TokenType.RBRACK;

            case ":" -> TokenType.COLON;

            case ";" -> TokenType.SCOLON;

            case "," -> TokenType.COMMA;


            case "." -> TokenType.DOT;

            case "+" -> TokenType.PLUS;

            case "-" -> TokenType.MINUS;

            case "*" -> TokenType.STAR;

            case "/" -> TokenType.SLASH;

            case "%" -> TokenType.MOD;

            case "**" -> TokenType.EXP;

            case "==" -> TokenType.EQUALS;

            case "=" -> TokenType.ASSIGN;

            case "!=" -> TokenType.NEQ;

            case "<=" -> TokenType.LE;

            case ">=" -> TokenType.GE;

            case "<" -> TokenType.LT;

            case ">" -> TokenType.GT;

            default -> throw new RuntimeException("Unknown Token: " + tokenText);
        };
    }
}
