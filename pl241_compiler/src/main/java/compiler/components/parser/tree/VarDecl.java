package compiler.components.parser.tree;

/**
 * varDecl = typeDecl ident { ',' ident } ';'
 */ 
public class VarDecl extends TreeNode {

	private Symbol symbol;

	public VarDecl(int lineNum, int charPos, Symbol symbol) {
		super(lineNum, charPos); 
		this.symbol = symbol;
	}

	public Symbol getSymbol() {
		return symbol;
	} 

	public String getValue() {
		return symbol.toString();
	}
}