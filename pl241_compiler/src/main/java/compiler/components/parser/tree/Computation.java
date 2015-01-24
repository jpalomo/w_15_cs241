package compiler.components.parser.tree;

import java.util.List;

/**
 * computation = 'main' {varDecl} {funcDecl} '{' statSequence '}' '.' 
 */
public class Computation extends TreeNode {

	private List<Statement> statSequence;
	private List<VarDecl> varDecl;
	private List<FuncDecl> funcDecl;

	public Computation(int lineNum, int charPos, List<VarDecl> varDecls, List<FuncDecl> funcDecls, List<Statement> statSequence) {
		super(lineNum, charPos);
	}
	
	public List<Statement> getStatSequence() {
		return statSequence;
	}

	public void setStatSequence(List<Statement> statSequence) {
		this.statSequence = statSequence;
	}

	public List<VarDecl> getVarDecl() {
		return varDecl;
	}

	public void setVarDecl(List<VarDecl> varDecl) {
		this.varDecl = varDecl;
	}

	public List<FuncDecl> getFuncDecl() {
		return funcDecl;
	}

	public void setFuncDecl(List<FuncDecl> funcDecl) {
		this.funcDecl = funcDecl;
	} 
}