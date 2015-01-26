package compiler.components.parser.tree;

/**
 * returnStatement = 'return' [ expression ]
 */
public class ReturnStatement extends TreeNode {

	private Expression expression;  //could be null
	
	public ReturnStatement(int lineNum, Expression expression) {
		super(lineNum);
	}

	public Expression getExpression() {
		return expression;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("return");
		return sb.append(expression.toString()).toString();
	}
}
