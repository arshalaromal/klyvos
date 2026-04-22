// Generated from D:/ArshalProjects/Klyvos/klyvos-core/src/main/antlr4/org/klyvos/parser/Klyvos.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link KlyvosParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface KlyvosVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(KlyvosParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(KlyvosParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(KlyvosParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#useStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUseStmt(KlyvosParser.UseStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(KlyvosParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#constDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDecl(KlyvosParser.ConstDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#returnStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(KlyvosParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#breakStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(KlyvosParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#continueStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(KlyvosParser.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#expStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpStmt(KlyvosParser.ExpStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(KlyvosParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#fnDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFnDecl(KlyvosParser.FnDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(KlyvosParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(KlyvosParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(KlyvosParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#whileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(KlyvosParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#forStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(KlyvosParser.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(KlyvosParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(KlyvosParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#logicalOr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOr(KlyvosParser.LogicalOrContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#logicalAnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalAnd(KlyvosParser.LogicalAndContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#equality}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquality(KlyvosParser.EqualityContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#comparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(KlyvosParser.ComparisonContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(KlyvosParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(KlyvosParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#exponent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExponent(KlyvosParser.ExponentContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary(KlyvosParser.UnaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#access}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAccess(KlyvosParser.AccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#postfix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfix(KlyvosParser.PostfixContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(KlyvosParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#grouping}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGrouping(KlyvosParser.GroupingContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(KlyvosParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(KlyvosParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList(KlyvosParser.ListContext ctx);
	/**
	 * Visit a parse tree produced by {@link KlyvosParser#argumentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentList(KlyvosParser.ArgumentListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SliceExprIndex}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSliceExprIndex(KlyvosParser.SliceExprIndexContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SliceExprEnd}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSliceExprEnd(KlyvosParser.SliceExprEndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SliceExprStart}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSliceExprStart(KlyvosParser.SliceExprStartContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SliceExprStartEnd}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSliceExprStartEnd(KlyvosParser.SliceExprStartEndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SliceExprStartStep}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSliceExprStartStep(KlyvosParser.SliceExprStartStepContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SliceExprEndStep}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSliceExprEndStep(KlyvosParser.SliceExprEndStepContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SliceExprStartEndStep}
	 * labeled alternative in {@link KlyvosParser#sliceExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSliceExprStartEndStep(KlyvosParser.SliceExprStartEndStepContext ctx);
}