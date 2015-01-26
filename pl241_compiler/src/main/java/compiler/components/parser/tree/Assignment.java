package compiler.components.parser.tree;

/**
 * assignment = 'let' designator '<-' expression
 */
public class Assignment extends TreeNode{
	private Designator designator;
	private Expression expression;

	public Assignment(int lineNum, Designator designator, Expression expression) {
		super(lineNum);
		this.designator = designator;
		this.expression = expression;
	}

	public Designator getDesignator() {
		return designator;
	}

	public Expression getExpression() {
		return expression;
	}

	/**
	 * returns a string representation of the designator and expression
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(designator.toString());
		sb.append(expression.toString());
		return sb.toString();
	}
}