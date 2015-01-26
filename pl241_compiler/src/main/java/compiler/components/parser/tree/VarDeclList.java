package compiler.components.parser.tree;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class VarDeclList extends TreeNode {

	private List<Symbol> declarations; //a list of variable declarations

	public VarDeclList(int lineNum, int charPos) {
		super(lineNum);
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

	@Override
	/**
	 * returns the value of the list of declarations as a string
	 */
	public String toString() {
		String decls = super.toString();
		if(declarations != null  && declarations.size() > 0){
			Joiner joiner = Joiner.on(":");
			joiner.skipNulls();
			decls =  joiner.join(declarations);
		}
        return decls; 
	}
}