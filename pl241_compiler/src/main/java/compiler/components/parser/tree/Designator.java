package compiler.components.parser.tree;

import java.util.List;

/**
 * designator = ident { '[' expression ']' }
 */
public class Designator extends TreeNode {

	private Ident ident;
	private List<Expression> expression;  //can be null
	
	public Designator(int lineNum, int charPos, Ident identifier, List<Expression> expression) {
		super(lineNum, charPos);
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
}