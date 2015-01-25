package compiler.components.parser.tree;

import java.util.List;

/**
 * funcDecl = ('function' | 'procedure') ident [formalParam] ';' funcBody ';'
 */
public class FuncDecl extends TreeNode {

	private Ident funcName;  //the name of the function/procedure
	private List<Ident> formalParams;
	private FuncBody funcBody;

	public FuncDecl(int lineNum, int charPos, Ident funcName, List<Ident> formalParams, FuncBody funcBody) {
		super(lineNum, charPos); 
		this.funcName = funcName;
		this.formalParams = formalParams;
		this.funcBody = funcBody;
	}

	public Ident getFuncName() {
		return funcName;
	}

	public List<Ident> getFormalParams() {
		return formalParams;
	}

	public FuncBody getFuncBody() {
		return funcBody;
	}
}