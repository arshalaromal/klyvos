// Generated from D:/ArshalProjects/Klyvos/klyvos-core/src/main/antlr4/org/klyvos/parser/Klyvos.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link KlyvosParser}.
 */
public interface KlyvosListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(KlyvosParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(KlyvosParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(KlyvosParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(KlyvosParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(KlyvosParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(KlyvosParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#useStmt}.
	 * @param ctx the parse tree
	 */
	void enterUseStmt(KlyvosParser.UseStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#useStmt}.
	 * @param ctx the parse tree
	 */
	void exitUseStmt(KlyvosParser.UseStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(KlyvosParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(KlyvosParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#constDecl}.
	 * @param ctx the parse tree
	 */
	void enterConstDecl(KlyvosParser.ConstDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#constDecl}.
	 * @param ctx the parse tree
	 */
	void exitConstDecl(KlyvosParser.ConstDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#returnStmt}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(KlyvosParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#returnStmt}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(KlyvosParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#breakStmt}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(KlyvosParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#breakStmt}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(KlyvosParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#continueStmt}.
	 * @param ctx the parse tree
	 */
	void enterContinueStmt(KlyvosParser.ContinueStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#continueStmt}.
	 * @param ctx the parse tree
	 */
	void exitContinueStmt(KlyvosParser.ContinueStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#expStmt}.
	 * @param ctx the parse tree
	 */
	void enterExpStmt(KlyvosParser.ExpStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#expStmt}.
	 * @param ctx the parse tree
	 */
	void exitExpStmt(KlyvosParser.ExpStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(KlyvosParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(KlyvosParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#fnDecl}.
	 * @param ctx the parse tree
	 */
	void enterFnDecl(KlyvosParser.FnDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#fnDecl}.
	 * @param ctx the parse tree
	 */
	void exitFnDecl(KlyvosParser.FnDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(KlyvosParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(KlyvosParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(KlyvosParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(KlyvosParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(KlyvosParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(KlyvosParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(KlyvosParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(KlyvosParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#forStmt}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(KlyvosParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#forStmt}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(KlyvosParser.ForStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(KlyvosParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(KlyvosParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(KlyvosParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(KlyvosParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#logicalOr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOr(KlyvosParser.LogicalOrContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#logicalOr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOr(KlyvosParser.LogicalOrContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#logicalAnd}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAnd(KlyvosParser.LogicalAndContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#logicalAnd}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAnd(KlyvosParser.LogicalAndContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#equality}.
	 * @param ctx the parse tree
	 */
	void enterEquality(KlyvosParser.EqualityContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#equality}.
	 * @param ctx the parse tree
	 */
	void exitEquality(KlyvosParser.EqualityContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(KlyvosParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(KlyvosParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(KlyvosParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(KlyvosParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(KlyvosParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(KlyvosParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#exponent}.
	 * @param ctx the parse tree
	 */
	void enterExponent(KlyvosParser.ExponentContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#exponent}.
	 * @param ctx the parse tree
	 */
	void exitExponent(KlyvosParser.ExponentContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#unary}.
	 * @param ctx the parse tree
	 */
	void enterUnary(KlyvosParser.UnaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#unary}.
	 * @param ctx the parse tree
	 */
	void exitUnary(KlyvosParser.UnaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#access}.
	 * @param ctx the parse tree
	 */
	void enterAccess(KlyvosParser.AccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#access}.
	 * @param ctx the parse tree
	 */
	void exitAccess(KlyvosParser.AccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#postfix}.
	 * @param ctx the parse tree
	 */
	void enterPostfix(KlyvosParser.PostfixContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#postfix}.
	 * @param ctx the parse tree
	 */
	void exitPostfix(KlyvosParser.PostfixContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(KlyvosParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(KlyvosParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#grouping}.
	 * @param ctx the parse tree
	 */
	void enterGrouping(KlyvosParser.GroupingContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#grouping}.
	 * @param ctx the parse tree
	 */
	void exitGrouping(KlyvosParser.GroupingContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(KlyvosParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(KlyvosParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(KlyvosParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(KlyvosParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#list}.
	 * @param ctx the parse tree
	 */
	void enterList(KlyvosParser.ListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#list}.
	 * @param ctx the parse tree
	 */
	void exitList(KlyvosParser.ListContext ctx);
	/**
	 * Enter a parse tree produced by {@link KlyvosParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(KlyvosParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link KlyvosParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(KlyvosParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SliceExprIndex}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void enterSliceExprIndex(KlyvosParser.SliceExprIndexContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SliceExprIndex}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void exitSliceExprIndex(KlyvosParser.SliceExprIndexContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SliceExprEnd}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void enterSliceExprEnd(KlyvosParser.SliceExprEndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SliceExprEnd}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void exitSliceExprEnd(KlyvosParser.SliceExprEndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SliceExprStart}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void enterSliceExprStart(KlyvosParser.SliceExprStartContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SliceExprStart}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void exitSliceExprStart(KlyvosParser.SliceExprStartContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SliceExprStartEnd}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void enterSliceExprStartEnd(KlyvosParser.SliceExprStartEndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SliceExprStartEnd}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void exitSliceExprStartEnd(KlyvosParser.SliceExprStartEndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SliceExprStartStep}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void enterSliceExprStartStep(KlyvosParser.SliceExprStartStepContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SliceExprStartStep}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void exitSliceExprStartStep(KlyvosParser.SliceExprStartStepContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SliceExprEndStep}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void enterSliceExprEndStep(KlyvosParser.SliceExprEndStepContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SliceExprEndStep}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void exitSliceExprEndStep(KlyvosParser.SliceExprEndStepContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SliceExprStartEndStep}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void enterSliceExprStartEndStep(KlyvosParser.SliceExprStartEndStepContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SliceExprStartEndStep}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 */
	void exitSliceExprStartEndStep(KlyvosParser.SliceExprStartEndStepContext ctx);
}