package compiler.components.parser.tree;

import java.util.List;

/**
 * funcDecl = ('function' | 'procedure') ident [formalParam] ';' funcBody ';'
 */
public class FuncDecl extends TreeNode {

	private Ident funcName;  //the name of the function/procedure
	private List<Ident> formalParams;
	private FuncBody funcBody;

	public FuncDecl(int lineNum, Ident funcName, List<Ident> formalParams, FuncBody funcBody) {
		super(lineNum); 
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(funcName);
		if(formalParams != null && formalParams.size() > 0) {
			sb.append("(");
			for(Ident ident: formalParams){
				sb.append(ident.toString());
			}
			sb.append("(");
		}
		sb.append(funcBody.toString());
		return sb.toString();
	}
}