package compiler.components.parser.tree;

import java.util.List;

/**
 * funcCall = 'call' ident [ '(' [expression { ',' expression } ] ')' ]
 */
public class FuncCall extends TreeNode {

	private Ident funcIdent;
	private List<Expression> expressions;
	
	public FuncCall(int lineNum, int charPos, Ident funcIdent, List<Expression> expressions) {
		super(lineNum, charPos);
		this.funcIdent = funcIdent;
		this.expressions = expressions;
	}

	public Ident getFuncIdent() {
		return funcIdent;
	}

	public List<Expression> getExpressions() {
		return expressions;
	}
}