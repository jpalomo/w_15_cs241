package compiler.components.parser.tree;

import java.util.ArrayList;
import java.util.List;

public class FuncDeclList extends TreeNode{

	private List<FuncDecl> functionDelcarations;
	
	public FuncDeclList(int lineNum, int charPos) {
		super(lineNum, charPos);
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
}
