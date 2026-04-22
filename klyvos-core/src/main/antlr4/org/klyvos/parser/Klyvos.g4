grammar Klyvos;



//PARSER RULES
program: NEWLINE* (line (NEWLINE+ line)* NEWLINE*)? EOF;

line
: statement COMMENT?  // Statement with optional comment on same line
| COMMENT            // Standalone comment
;


statement
    :useStmt
    |varDecl
    |constDecl
    |fnDecl
    |expStmt
    |ifStmt
    |whileStmt
    |forStmt
    |breakStmt
    |continueStmt
    |block
    |returnStmt;

useStmt: USE STRING ;                               //use "math"
varDecl: VAR IDENTIFIER (ASSIGN expression)? ;      //var name=45
constDecl: CONST IDENTIFIER ASSIGN expression ;     //const c=45
returnStmt: RETURN expression? ;
breakStmt: BREAK ;
continueStmt: CONTINUE ;
expStmt: expression ;
block: LBRACE NEWLINE* (statement (NEWLINE+ statement)*)? NEWLINE* RBRACE;
fnDecl: FN IDENTIFIER LPAREN paramList? RPAREN block;
paramList: parameter (COMMA parameter)*;
parameter: IDENTIFIER (ASSIGN expression)?;
ifStmt: IF LPAREN expression RPAREN block (ELSE ifStmt)?
      | IF LPAREN expression RPAREN block (ELSE block)?;
whileStmt: WHILE LPAREN expression RPAREN block;
forStmt: FOR LPAREN IDENTIFIER IN expression RPAREN block;

//operator precedence

expression: assignment;
assignment:IDENTIFIER ASSIGN assignment
          |logicalOr;
logicalOr: logicalAnd (OR logicalAnd)*;
logicalAnd: equality(AND equality)*;
equality: comparison ((EQUALS|NEQ)comparison)*;
comparison: term ((LT|LE|GT|GE|IN)term)*;
term: factor((PLUS|MINUS)factor)*;
factor: exponent((STAR|SLASH|MOD)exponent)*;
exponent: unary(EXP unary)*;
unary:(NOT|MINUS)unary | access;
access: atom postfix*;
postfix: LPAREN (argumentList)? RPAREN //function call
       | DOT IDENTIFIER //property access
       | LBRACK sliceExpr RBRACK; //indexing/slicing

literal: DECIMAL|INTEGER | STRING | BOOLEAN | NULL;
grouping: LPAREN expression RPAREN ;

variable: IDENTIFIER;

atom: literal|list|variable|grouping;


list: LBRACK NEWLINE* (expression (COMMA NEWLINE* expression)*)? NEWLINE* RBRACK;

argumentList: expression (COMMA expression)*;
sliceExpr
    : exprOnly=expression                                      # SliceExprIndex
    | colonStart=COLON end=expression                          # SliceExprEnd
    | start=expression COLON                                   # SliceExprStart
    | start=expression COLON end=expression                    # SliceExprStartEnd
    | start=expression COLON COLON step=expression             # SliceExprStartStep
    | COLON end=expression COLON step=expression               # SliceExprEndStep
    | start=expression COLON end=expression COLON step=expression # SliceExprStartEndStep
    ;



//LEXER RULES
//Keywords
USE: 'use';
VAR: 'var';
CONST: 'const';
NULL: 'null';
FN: 'fn';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
FOR: 'for';
IN: 'in';
AND: 'and';
OR: 'or';
NOT: 'not';
BREAK: 'break';
CONTINUE: 'continue';
RETURN: 'return';
BOOLEAN: 'true'|'false';

//Literals
STRING: '"' ( ~["\\] | '\\' (["\\nt] | 'u' HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT) )* '"' ;

fragment HEXDIGIT: [0-9a-fA-F] ;
DECIMAL : [0-9]+ '.' [0-9]+ ;
INTEGER : [0-9]+ ;


IDENTIFIER: [a-zA-Z_][a-zA-Z_0-9]*;

//Operators
PLUS: '+';
MINUS: '-';
SLASH: '/';
EXP: '**';
STAR: '*';
MOD: '%';
EQUALS: '==';
ASSIGN: '=';
NEQ: '!=';
LE: '<=';
GE: '>=';
LT: '<';
GT: '>';
//Delimiters
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
LBRACK: '[';
RBRACK: ']';

DOT: '.';
COMMA: ',';
COLON: ':';
SCOLON: ';';

//Skip
COMMENT : '#' ~[\r\n]*;
NEWLINE:('\r'? '\n')+;
WS : [ \t]+ -> skip ;