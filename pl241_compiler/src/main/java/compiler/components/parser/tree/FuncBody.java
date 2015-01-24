package compiler.components.parser.tree;

import java.util.List;

/**
 * funcBody = { varDecl } '{' [statSequence] '}' 
 */ 
public class FuncBody extends TreeNode {

	private List<VarDecl> varDecls;
	private List<Statement> statements;
	
	public FuncBody(int lineNum, int charPos, List<VarDecl> varDecls, List<Statement> statements) {
		super(lineNum, charPos);
		this.varDecls = varDecls;
		this.statements = statements;
	}

	public List<VarDecl> getVarDecls() {
		return varDecls;
	}

	public List<Statement> getStatements() {
		return statements;
	} 
}