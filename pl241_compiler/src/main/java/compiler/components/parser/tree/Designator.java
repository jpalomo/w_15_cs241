package compiler.components.parser.tree;

import java.util.List;

/**
 * designator = ident { '[' expression ']' }
 */
public class Designator extends Factor {

	List<Expression> arrayExprs;

	public Designator(int lineNum, int charPos, Symbol identifier, FactorType type) {
		super(lineNum, charPos, type);
	} 

	public List<Expression> getArrayExprs() {
		return arrayExprs;
	}

	public void setArrayExprs(List<Expression> arrayExprs) {
		this.arrayExprs = arrayExprs;
	} 

	public enum DesignatorType {
		IDENT(), ARRAY();
	}
}