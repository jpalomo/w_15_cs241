package compiler.components.parser.tree;

import java.util.List;

/**
 * funcCall = 'call' ident [ '(' [expression { ',' expression } ] ')' ]
 */
public class FuncCall extends Factor {

	private Symbol funcIdent;
	private List<Expression> expressions;
	
	public FuncCall(int lineNum, int charPos, Symbol funcIdent, List<Expression> expressions) {
		super(lineNum, charPos, FactorType.FUNCCALL);
		this.funcIdent = funcIdent;
		this.expressions = expressions;
	}

	public Symbol getFuncIdent() {
		return funcIdent;
	}

	public List<Expression> getExpressions() {
		return expressions;
	}
}