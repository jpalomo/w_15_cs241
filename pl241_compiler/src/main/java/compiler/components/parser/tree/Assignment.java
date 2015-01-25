package compiler.components.parser.tree;

/**
 * assignment = 'let' designator '<-' expression
 */
public class Assignment extends TreeNode{
	private Designator designator;
	private Expression expression;

	public Assignment(int lineNum, int charPos, Designator designator, Expression expression) {
		super(lineNum, charPos);
	}

	public Designator getDesignator() {
		return designator;
	}

	public Expression getExpression() {
		return expression;
	}
}
