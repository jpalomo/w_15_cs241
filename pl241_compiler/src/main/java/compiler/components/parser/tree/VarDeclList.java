package compiler.components.parser.tree;

import java.util.ArrayList;
import java.util.List;

public class VarDeclList extends TreeNode {

	private List<Symbol> declarations; //a list of variable declarations

	public VarDeclList(int lineNum, int charPos) {
		super(lineNum, charPos);
		declarations = new ArrayList<Symbol>();
	}

	public void add(Symbol varDecl) {
		declarations.add(varDecl);
	}

	public List<Symbol> getDeclarations() {
		return declarations;
	} 

	public void addAll(List<Symbol> varDeclList) {
		declarations.addAll(varDeclList);
	}
}
