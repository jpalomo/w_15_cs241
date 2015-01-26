package compiler.components.parser.tree;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class FuncDeclList extends TreeNode{

	private List<FuncDecl> functionDelcarations;
	
	public FuncDeclList(int lineNum, int charPos) {
		super(lineNum);
		functionDelcarations = new ArrayList<FuncDecl>();
	}

	public void add(FuncDecl funcDecl){
		functionDelcarations.add(funcDecl);
	}

	public List<FuncDecl> getDeclarations() {
		return functionDelcarations;
	} 

	public void add(List<FuncDecl> funcDecls) {
		functionDelcarations.addAll(funcDecls);
	}

	/**
	 * returns a string representation of the function declarations
	 */
	public String toString() {
		Joiner joiner = Joiner.on(" ");
		joiner.join(functionDelcarations);
		return joiner.toString();
	}
}
