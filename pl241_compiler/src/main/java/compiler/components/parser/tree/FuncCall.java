package compiler.components.parser.tree;

import java.util.List;

import com.google.common.base.Joiner;

/**
 * funcCall = 'call' ident [ '(' [expression { ',' expression } ] ')' ]
 */
public class FuncCall extends TreeNode {

	private Ident funcIdent;
	private List<Expression> expressions;
	
	public FuncCall(int lineNum, Ident funcIdent, List<Expression> expressions) {
		super(lineNum);
		this.funcIdent = funcIdent;
		this.expressions = expressions;
	}

	public Ident getFuncIdent() {
		return funcIdent;
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	/**
	 * returns a string representation of the Ident and expression of the funcall
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();	
		sb.append(funcIdent.toString());
		Joiner joiner = Joiner.on(" ");
		String expressionsStrings = joiner.join(expressions);
		sb.append(" " + expressionsStrings);
		return sb.toString();
	}
}