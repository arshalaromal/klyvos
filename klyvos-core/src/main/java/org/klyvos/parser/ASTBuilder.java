package org.klyvos.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.klyvos.ast.ASTNode;
import org.klyvos.ast.Expression;
import org.klyvos.ast.Program;
import org.klyvos.ast.Statement;
import org.klyvos.ast.expressions.*;
import org.klyvos.ast.statements.*;
import org.klyvos.error.ErrorExpr;
import org.klyvos.error.ErrorStmt;
import org.klyvos.error.KlyvosCompileError;
import org.klyvos.tokens.Token;
import org.klyvos.tokens.TokenType;
import org.klyvos.vm.ValueType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arshal Aromal
 */
public class ASTBuilder extends KlyvosBaseVisitor<ASTNode> {

    //Different Expressions


    @Override
    public ASTNode visitProgram(KlyvosParser.ProgramContext ctx) {

        List<Statement> statements = new ArrayList<>();
        for (KlyvosParser.LineContext lineCtx : ctx.line()) {
            if (lineCtx.statement() != null) {
                Statement stmt = (Statement) lineCtx.statement().accept(this);
                if (stmt != null) {
                    statements.add(stmt);
                }
            }

        }
        return new Program(statements);
    }

    @Override
    public Expression visitLiteral(KlyvosParser.LiteralContext ctx) {

        //DECIMAL
        if (ctx.DECIMAL() != null) {
            String text = ctx.DECIMAL().getText();
            double number = Double.parseDouble(text);

            return new LiteralExpr(
                    new Token(TokenType.DECIMAL, text, number, ctx.DECIMAL().getSymbol().getLine()),
                    ValueType.DECIMAL);

        }
        //INTEGER
        else if (ctx.INTEGER() != null) {
            String text = ctx.INTEGER().getText();
            long value = Long.parseLong(text);
            return new LiteralExpr(
                    new Token(TokenType.INTEGER, text, value, ctx.INTEGER().getSymbol().getLine()),
                    ValueType.INTEGER);
        }
        //STRING
        else if (ctx.STRING() != null) {
            String text = ctx.STRING().getText();
            String unescaped = unescapeString(text.substring(1, text.length() - 1)); // remove quotes and unescape
            return new LiteralExpr(new Token(
                    TokenType.STRING,
                    text,
                    unescaped,
                    ctx.STRING().getSymbol().getLine()),
                    ValueType.STRING);
        }
        //BOOLEAN
        else if (ctx.BOOLEAN() != null) {
            String text = ctx.BOOLEAN().getText();
            boolean value = Boolean.parseBoolean(text);
            return new LiteralExpr(new Token(
                    TokenType.BOOLEAN,
                    text,
                    value,
                    ctx.BOOLEAN().getSymbol().getLine()),
                    ValueType.BOOLEAN);
        }
        //NULL
        else if (ctx.NULL() != null) {
            return new LiteralExpr(new Token(
                    TokenType.NULL,
                    ctx.NULL().getText(),
                    null,
                    ctx.NULL().getSymbol().getLine()),
                    ValueType.NULL);
        }

        return new ErrorExpr(new KlyvosCompileError(
                "LiteralError",
                "Unknown literal type: " + ctx.getText(),
                ctx.getStart().getLine()
        ));
    }

    public static String unescapeString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(++i);
                switch (next) {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '"':
                        sb.append('"');
                        break;
                    case '\'':
                        sb.append('\'');
                        break;
                    default:
                        sb.append(next);
                        break; // or throw error for unknown escape
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    @Override
    public ASTNode visitVariable(KlyvosParser.VariableContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        Token name = new Token(TokenType.IDENTIFIER, varName, varName,
                ctx.IDENTIFIER().getSymbol().getLine());

        return new VariableExpr(name);
    }

    @Override
    public ASTNode visitGrouping(KlyvosParser.GroupingContext ctx) {
        Expression innerExpression = (Expression) visit(ctx.expression());
        return new GroupingExpr(innerExpression, ctx.start.getLine());
    }

    @Override
    public ASTNode visitUnary(KlyvosParser.UnaryContext ctx) {
        if (ctx.NOT() != null || ctx.MINUS() != null) {
            //Operator Text
            String opText = ctx.NOT() != null ? ctx.NOT().getText() : ctx.MINUS().getText();
            TokenType tokenType = Token.toTokenType(opText);
            Token operator = new Token(tokenType, opText, opText, ctx.getStart().getLine());
            Expression right = (Expression) visit(ctx.unary());
            return new UnaryExpr(operator, right);
        }

        return visit(ctx.access());
    }

    //visitBinary
    @Override
    public ASTNode visitTerm(KlyvosParser.TermContext ctx) {
        Expression left = (Expression) visit(ctx.factor(0));
        for (int i = 1; i < ctx.factor().size(); i++) {
            /**
             * Constructs a binary expression chain from left to right for expressions like:
             *     a + b - c + d ...
             * The rule in the grammar looks like:
             *     term: factor ((PLUS | MINUS) factor)* ;
             * ANTLR parses this into a tree like:
             *     [factor, op, factor, op, factor, ...]
             * e.g. for `1 + 2 - 3`, ctx.children looks like:
             *     [1, '+', 2, '-', 3]
             * So:
             * - ctx.factor(0) → first value (1)
             * - ctx.factor(1) → second value (2)
             * - ctx.factor(2) → third value (3)
             * Operators are located at positions:
             * - 2 * 1 - 1 = 1 → '+'
             * - 2 * 2 - 1 = 3 → '-'
             * Hence, to match each factor(i) with the correct operator between it and the previous,
             * we use: ctx.getChild(2 * i - 1)
             */
            Expression right = (Expression) visit(ctx.factor(i));

            String opText = ctx.getChild(2 * i - 1).getText();

            TokenType type = Token.toTokenType(opText);


            Token operator = new Token(type, opText, null, ((TerminalNode) ctx.getChild(2 * i - 1)).getSymbol().getLine());

            left = new BinaryExpr(left, operator, right);
        }
        return left;
    }


    @Override
    public ASTNode visitAccess(KlyvosParser.AccessContext ctx) {


        Expression expression = (Expression) visit(ctx.atom());
        for (KlyvosParser.PostfixContext postfix : ctx.postfix()) {
            if (postfix.LPAREN() != null) {
                //Function or Method Call
                List<Expression> arguments = new ArrayList<>();
                if (postfix.argumentList() != null) {
                    for (var argCtx : postfix.argumentList().expression()) {
                        arguments.add((Expression) visit(argCtx));
                    }
                }
                expression = new CallExpr(expression, arguments,
                        postfix.getStart().getLine());

            } else if (postfix.DOT() != null) {

                //expression: the base
                //name: what is after dot
                //Property Access
                String name = postfix.IDENTIFIER().getText();

                Token token = new Token(TokenType.IDENTIFIER, name, name,
                        postfix.IDENTIFIER().getSymbol().getLine());

                expression = new GetExpr(expression, token);
                //Indexing
            } else if (postfix.LBRACK() != null) {

                var slice = postfix.sliceExpr();
                Expression start = null, end = null, step = null;

                if (slice instanceof KlyvosParser.SliceExprIndexContext se) {
                    start = (Expression) visit(se.exprOnly);
                } else if (slice instanceof KlyvosParser.SliceExprEndContext se) {
                    end = (Expression) visit(se.end);
                } else if (slice instanceof KlyvosParser.SliceExprStartContext se) {
                    start = (Expression) visit(se.start);
                } else if (slice instanceof KlyvosParser.SliceExprStartEndContext se) {
                    start = (Expression) visit(se.start);
                    end = (Expression) visit(se.end);
                } else if (slice instanceof KlyvosParser.SliceExprStartStepContext se) {
                    start = (Expression) visit(se.start);
                    step = (Expression) visit(se.step);
                } else if (slice instanceof KlyvosParser.SliceExprEndStepContext se) {
                    end = (Expression) visit(se.end);
                    step = (Expression) visit(se.step);
                } else if (slice instanceof KlyvosParser.SliceExprStartEndStepContext se) {
                    start = (Expression) visit(se.start);
                    end = (Expression) visit(se.end);
                    step = (Expression) visit(se.step);
                }

                expression = new IndexExpr(expression, start, end, step, slice.getStart().getLine());

            }
        }
        return expression;
    }


    @Override
    public ASTNode visitList(KlyvosParser.ListContext ctx) {
        List<Expression> elements = new ArrayList<>();
        if (ctx.expression() != null && !ctx.expression().isEmpty()) {
            for (var exprCtx : ctx.expression()) {
                elements.add((Expression) visit(exprCtx));
            }
        }
        return new ListExpr(elements, ctx.getStart().getLine());
    }


    @Override
    public ASTNode visitAssignment(KlyvosParser.AssignmentContext ctx) {
        /*In grammar, assignment is defined as:
          assignment:IDENTIFIER ASSIGN assignment
                    |logicalOr;
          so we check identifier and assign is null or not. Or else it will be logicalOr
         */
        if (ctx.IDENTIFIER() != null && ctx.ASSIGN() != null) {
            String name = ctx.IDENTIFIER().getText();
            Token token = new Token(TokenType.IDENTIFIER, name, name,
                    ctx.IDENTIFIER().getSymbol().getLine());

            Expression value = (Expression) visit(ctx.assignment());
            return new AssignExpr(token, value);
        } else {
            return visit(ctx.logicalOr());
        }
    }

    @Override
    public ASTNode visitLogicalOr(KlyvosParser.LogicalOrContext ctx) {
        Expression expr = (Expression) visit(ctx.logicalAnd(0));

        for (int i = 1; i < ctx.logicalAnd().size(); i++) {
            //Get the ith OR
            org.antlr.v4.runtime.Token opToken = ctx.OR(i - 1).getSymbol();

            Token token = new Token(TokenType.OR, opToken.getText(), null, opToken.getLine());
            Expression right = (Expression) visitLogicalAnd(ctx.logicalAnd(i));
            expr = new LogicalExpr(expr, token, right);

        }
        return expr;
    }

    @Override
    public ASTNode visitLogicalAnd(KlyvosParser.LogicalAndContext ctx) {
        Expression expr = (Expression) visit(ctx.equality(0));

        for (int i = 1; i < ctx.equality().size(); i++) {
            TerminalNode andNode = ctx.AND(i - 1);
            org.antlr.v4.runtime.Token opToken = andNode.getSymbol();

            Token token = new Token(
                    TokenType.AND,
                    opToken.getText(),
                    null,
                    opToken.getLine()
            );

            Expression right = (Expression) visit(ctx.equality(i));
            expr = new LogicalExpr(expr, token, right);
        }

        return expr;
    }

    @Override
    public Expression visitEquality(KlyvosParser.EqualityContext ctx) {
        Expression expr = (Expression) visit(ctx.comparison(0));

        for (int i = 1; i < ctx.comparison().size(); i++) {
            ParseTree opNode = ctx.getChild((i * 2) - 1); // Operator is at every odd index
            org.antlr.v4.runtime.Token antlrToken = ((TerminalNode) opNode).getSymbol();

            TokenType type = Token.toTokenType(antlrToken.getText());

            Token operator = new Token(type, antlrToken.getText(), null, antlrToken.getLine());
            Expression right = (Expression) visit(ctx.comparison(i));
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }

    @Override
    public Expression visitComparison(KlyvosParser.ComparisonContext ctx) {
        Expression expr = (Expression) visit(ctx.term(0));

        for (int i = 1; i < ctx.term().size(); i++) {
            ParseTree opNode = ctx.getChild((i * 2) - 1);
            org.antlr.v4.runtime.Token antlrToken = ((TerminalNode) opNode).getSymbol();

            TokenType type = Token.toTokenType(antlrToken.getText());

            Token operator = new Token(type, antlrToken.getText(), null, antlrToken.getLine());
            Expression right = (Expression) visit(ctx.term(i));
            expr = new BinaryExpr(expr, operator, right);
        }

        return expr;
    }


    @Override
    public ASTNode visitFactor(KlyvosParser.FactorContext ctx) {
        /*
        Checking for single operand is necessary
        factor: exponent ((STAR | SLASH | MOD) exponent)*;
        The ((STAR | SLASH | MOD) exponent)* part is optional and can repeat zero times.
        so the rule accepts single operands
         */

        List<KlyvosParser.ExponentContext> operands = ctx.exponent();
        if (operands.size() == 1) {
            return visit(operands.getFirst());
        }
        Expression left = (Expression) visit(operands.getFirst());

        for (int i = 1; i < operands.size(); i++) {
            Expression right = (Expression) visit(operands.get(i));

            // Operator appears between left and right, at index (i * 2) - 1
            var tNode = ((TerminalNode) ctx.getChild((i * 2) - 1)).getSymbol();
            String opToken = tNode.getText();
            TokenType tokenType = Token.toTokenType(opToken);
            Token token = new Token(tokenType, opToken, null, tNode.getLine());
            left = new BinaryExpr(left, token, right);
        }
        return left;
    }


    @Override
    public ASTNode visitExponent(KlyvosParser.ExponentContext ctx) {
        List<KlyvosParser.UnaryContext> operands = ctx.unary();
        if (operands.size() == 1) {
            return visit(operands.get(0));
        }

        // Exponentiation is **right-associative** (e.g., 2 ** 3 ** 2 = 2 ** (3 ** 2))
        Expression right = (Expression) visit(operands.get(operands.size() - 1));

        for (int i = operands.size() - 2; i >= 0; i--) {
            Expression left = (Expression) visit(operands.get(i));
            Token opToken = new Token(TokenType.EXP, "**", null, operands.get(i).getStart().getLine());
            right = new BinaryExpr(left, opToken, right);
        }
        return right;
    }


    //Different Statements

    @Override
    public Statement visitVarDecl(KlyvosParser.VarDeclContext ctx) {
        Token name = new Token(TokenType.IDENTIFIER, ctx.IDENTIFIER().getText(), ctx.IDENTIFIER().getText(),
                ctx.IDENTIFIER().getSymbol().getLine());

        Token varToken = new Token(TokenType.VAR, ctx.VAR().getText(),
                null, ctx.VAR().getSymbol().getLine());

        Expression initializer = null;
        if (ctx.expression() != null) {
            initializer = (Expression) visit(ctx.expression());
        }

        return new VarDeclStmt(varToken, name, initializer);
    }

    @Override
    public Statement visitConstDecl(KlyvosParser.ConstDeclContext ctx) {

        Token name = new Token(TokenType.IDENTIFIER, ctx.IDENTIFIER().getText(),
                ctx.IDENTIFIER().getText(), ctx.IDENTIFIER().getSymbol().getLine());

        Token constToken = new Token(TokenType.CONST, ctx.CONST().getText(),
                null, ctx.CONST().getSymbol().getLine());

        Expression value = (Expression) visit(ctx.expression());

        return new ConstDeclStmt(constToken, name, value);

    }


    @Override
    public Statement visitBlock(KlyvosParser.BlockContext ctx) {

        List<Statement> statements = new ArrayList<>();
        for (var stmtCtx : ctx.statement()) {
            Statement statement = (Statement) visit(stmtCtx);
            if (statement != null) {
                statements.add(statement);
            }
        }
        return new BlockStmt(statements);
    }

    @Override
    public ASTNode visitFnDecl(KlyvosParser.FnDeclContext ctx) {


        Token name = new Token(TokenType.IDENTIFIER, ctx.IDENTIFIER().getText(),
                ctx.IDENTIFIER().getText(), ctx.IDENTIFIER().getSymbol().getLine());

        Token fnToken = new Token(TokenType.FN, ctx.FN().getText(),
                null, ctx.FN().getSymbol().getLine());

        List<FnDeclStmt.Parameter> parameters = new ArrayList<>();

        if (ctx.paramList() != null) {
            boolean seenDefault = false;

            for (KlyvosParser.ParameterContext pctx : ctx.paramList().parameter()) {
                var antlrToken = pctx.IDENTIFIER().getSymbol();
                Token paramName = new Token(TokenType.IDENTIFIER, antlrToken.getText(),
                        antlrToken.getText(), antlrToken.getLine());

                Expression defaultValue = null;
                if (pctx.expression() != null) {
                    defaultValue = (Expression) visit(pctx.expression());
                    seenDefault = true;
                } else if (seenDefault) {
                    // Invalid: required param after default
                    return new ErrorStmt(new KlyvosCompileError(
                            "ParameterError",
                            "In function '" + name.lexeme + "': non-default parameter '" + paramName.lexeme + "' cannot follow a default parameter.",
                            paramName.line
                    ));
                }

                parameters.add(new FnDeclStmt.Parameter(paramName, defaultValue));
            }
        }

        BlockStmt body = (BlockStmt) visit(ctx.block());
        return new FnDeclStmt(fnToken, name, parameters, body);
    }


    @Override
    public ASTNode visitForStmt(KlyvosParser.ForStmtContext ctx) {
        var antlrToken = ctx.IDENTIFIER().getSymbol();
        Token varToken = new Token(TokenType.IDENTIFIER, antlrToken.getText(),
                antlrToken.getText(), antlrToken.getLine());

        Token forToken = new Token(TokenType.FOR, ctx.FOR().getText(),
                null, ctx.FOR().getSymbol().getLine());

        Expression iterable = (Expression) visit(ctx.expression());
        BlockStmt body = (BlockStmt) visit(ctx.block());
        return new ForStmt(forToken, varToken, iterable, body);
    }


    @Override
    public ASTNode visitIfStmt(KlyvosParser.IfStmtContext ctx) {
        Token ifToken = new Token(
                TokenType.IF,
                ctx.IF().getText(),
                null,
                ctx.IF().getSymbol().getLine()
        );

        Expression condition = (Expression) visit(ctx.expression());
        BlockStmt thenBranch = (BlockStmt) visit(ctx.block(0));
        Statement elseBranch = null;

        if (ctx.ELSE() != null) {
            if (ctx.ifStmt() != null) {
                // ⬇ Desugar `else if` into `else { if (...) ... }`
                Statement nestedIf = (Statement) visit(ctx.ifStmt());
                elseBranch = new BlockStmt(List.of(nestedIf));
            } else if (ctx.block().size() > 1) {
                elseBranch = (Statement) visit(ctx.block(1));
            }
        }

        return new IfStmt(ifToken, condition, thenBranch, elseBranch);
    }


    @Override
    public ASTNode visitWhileStmt(KlyvosParser.WhileStmtContext ctx) {
        Token whileToken = new Token(TokenType.WHILE, ctx.WHILE().getText(),
                null, ctx.WHILE().getSymbol().getLine());
        Expression condition = (Expression) visit(ctx.expression());
        BlockStmt body = (BlockStmt) visit(ctx.block());
        return new WhileStmt(whileToken, condition, body);
    }

    @Override
    public ASTNode visitBreakStmt(KlyvosParser.BreakStmtContext ctx) {
        Token keyword = new Token(TokenType.BREAK, ctx.BREAK().getText(),
                null, ctx.BREAK().getSymbol().getLine());
        return new BreakStmt(keyword);
    }

    @Override
    public ASTNode visitContinueStmt(KlyvosParser.ContinueStmtContext ctx) {
        Token keyword = new Token(TokenType.CONTINUE, ctx.CONTINUE().getText(),
                null, ctx.CONTINUE().getSymbol().getLine());
        return new ContinueStmt(keyword);
    }

    @Override
    public ASTNode visitReturnStmt(KlyvosParser.ReturnStmtContext ctx) {
        Token keyword = new Token(TokenType.RETURN, ctx.RETURN().getText()
                , null, ctx.RETURN().getSymbol().getLine());
        Expression value = ctx.expression() != null ? (Expression) visit(ctx.expression()) : null;

        return new ReturnStmt(keyword, value);
    }

    @Override
    public Statement visitExpStmt(KlyvosParser.ExpStmtContext ctx) {
        Expression expr = (Expression) visit(ctx.expression());
        return new ExpressionStmt(expr);
    }

    @Override
    public ASTNode visitUseStmt(KlyvosParser.UseStmtContext ctx) {
        String n = ctx.STRING().getText();
        Token moduleToken = new Token(TokenType.STRING, n,
                n.substring(1, n.length() - 1)
                , ctx.STRING().getSymbol().getLine());
        Token keyword = new Token(TokenType.USE, ctx.USE().getText(),
                null, ctx.USE().getSymbol().getLine());
        return new UseStmt(keyword, moduleToken);
    }


}