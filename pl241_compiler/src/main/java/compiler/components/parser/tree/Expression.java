package compiler.components.parser.tree;

/**
 * expression = term  { ('+' | '-') term }
 */ 
public class Expression extends TreeNode {

	private int value;
	private Symbol symbol;
	private ExpressionType type;
	private String expression;

	public Expression(int lineNum, int charPos, ExpressionType type) {
		super(lineNum, charPos);
		this.type = type;
	}

	public Expression(int lineNum, int charPos, int value, ExpressionType type) {
		super(lineNum, charPos);
		this.type = type;
		this.value = value;
	}

	public enum ExpressionType {
		NUMBER(), FUNCCALL(), EXPRESSION();
	}

	/**
	 * Sets the expression as a string for parsing later 
	 * @param factor1
	 * @param factor2
	 * @param op
	 */
	public void setExpression(Factor factor1, Factor factor2, Symbol op){
		StringBuilder sb = new StringBuilder();
		sb.append(factor1.getFactorIdent().toString());
		sb.append(op.toString());
		sb.append(factor2.getFactorIdent().toString());
	}

	/**
	 * 
	 * @return a string representation of the expression
	 * (e.g. x*y, x*2, 2*x)
	 */
	public String getExpressionAsString() {
		return expression;
	}

	public int getValue() {
		return value;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public ExpressionType getType() {
		return type;
	}

	public String getExpression() {
		return expression;
	}
}