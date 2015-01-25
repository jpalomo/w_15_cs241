package compiler.components.parser.tree;

/**
 * returnStatement = 'return' [ expression ]
 */
public class ReturnStatement extends TreeNode {

	private Expression expression;  //could be null
	
	public ReturnStatement(int lineNum, int charPos, Expression expression) {
		super(lineNum, charPos);
	}

	public Expression getExpression() {
		return expression;
	}
}
