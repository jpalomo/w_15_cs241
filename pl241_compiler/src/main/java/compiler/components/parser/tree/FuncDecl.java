package compiler.components.parser.tree;

import java.util.List;

/**
 * funcDecl = ('function' | 'procedure') ident [formalParam] ';' funcBody ';'
 */
public class FuncDecl extends TreeNode {

	Symbol funcName;
	List<Symbol> formalParams;
	FuncBody funcBody;

	public FuncDecl(int lineNum, int charPos, Symbol funcName, List<Symbol> formalParams, FuncBody funcBody) {
		super(lineNum, charPos); 
		this.funcName = funcName;
		this.formalParams = formalParams;
		this.funcBody = funcBody;
	}
}