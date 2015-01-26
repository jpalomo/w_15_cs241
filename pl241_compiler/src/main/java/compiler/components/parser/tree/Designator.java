package compiler.components.parser.tree;

import java.util.List;

/**
 * designator = ident { '[' expression ']' }
 */
public class Designator extends TreeNode {

	private Ident ident;
	private List<Expression> expression;  //can be null
	
	public Designator(int lineNum, Ident identifier, List<Expression> expression) {
		super(lineNum);
		this.expression = expression;
		this.ident = identifier;
	}

	/**
	 * Expression can be null
	 * @return
	 */
	public List<Expression> getExpression() {
		return expression;
	}

	public Ident getIdent() {
		return ident;
	} 

	/**
	 * returns a string representing the identifier and expression (if array)
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(ident.toString());

		if(expression != null && expression.size() > 0) {
			sb.append(expression.toString());
		}
		return sb.toString();
	}
}