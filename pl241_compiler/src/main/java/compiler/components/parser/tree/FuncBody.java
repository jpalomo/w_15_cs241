package compiler.components.parser.tree;

import java.util.List;

import com.google.common.base.Joiner;

/**
 * funcBody = { varDecl } '{' [statSequence] '}' 
 */ 
public class FuncBody extends TreeNode {

	private List<VarDecl> varDecls;
	private List<Statement> statements;
	
	public FuncBody(int lineNum, List<VarDecl> varDecls, List<Statement> statements) {
		super(lineNum);
		this.varDecls = varDecls;
		this.statements = statements;
	}

	public List<VarDecl> getVarDecls() {
		return varDecls;
	}

	public List<Statement> getStatements() {
		return statements;
	} 

	public String toString() {
		Joiner joiner = Joiner.on(" ");
		joiner.join(varDecls);
		joiner.join(statements);
		return joiner.toString();
	}
}