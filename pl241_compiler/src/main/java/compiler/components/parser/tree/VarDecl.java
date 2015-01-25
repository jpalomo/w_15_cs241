package compiler.components.parser.tree;

/**
 * varDecl = typeDecl ident { ',' ident } ';'
 */ 
public class VarDecl extends TreeNode {

	private Ident ident;

	public VarDecl(int lineNum, int charPos, Ident ident) {
		super(lineNum, charPos); 
		this.ident = ident;
	}

	public Ident getIdent() {
		return ident;
	} 
}