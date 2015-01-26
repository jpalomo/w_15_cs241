package compiler.components.parser.tree;

/**
 * varDecl = typeDecl ident { ',' ident } ';'
 */ 
public class VarDecl extends TreeNode {

	private Ident ident;

	public VarDecl(int lineNum, Ident ident) {
		super(lineNum); 
		this.ident = ident;
	}

	public Ident getIdent() {
		return ident;
	} 

	@Override
	/**
	 * returns the value of Ident as a string
	 */
	public String toString() {
		return ident.toString();
	}
}