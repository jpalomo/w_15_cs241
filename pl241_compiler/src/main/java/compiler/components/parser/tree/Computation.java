package compiler.components.parser.tree;

import java.util.List;

/**
 * computation = 'main' {varDecl} {funcDecl} '{' statSequence '}' '.' 
 */
public class Computation extends TreeNode {

	private List<Statement> statSequence;
	private List<VarDecl> varDecls;
	private List<FuncDecl> funcDecls;

	public Computation(int lineNum, int charPos, List<VarDecl> varDecls, List<FuncDecl> funcDecls, List<Statement> statSequence) {
		super(lineNum, charPos);
		this.varDecls = varDecls;
		this.funcDecls = funcDecls;
		this.statSequence = statSequence; 
	}
	
	public List<Statement> getStatSequence() {
		return statSequence;
	}

	public List<VarDecl> getVarDecl() {
		return varDecls;
	}

	public List<FuncDecl> getFuncDecl() {
		return funcDecls;
	}
}