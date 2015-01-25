package compiler.components.parser.tree;

public class Ident extends TreeNode {

	private Symbol symbol;

	public Ident(int lineNum, int charPos, Symbol symbol) {
		super(lineNum, charPos);
		this.symbol = symbol;
	}
	
	public String getIdentAsString() {
		return symbol.toString();
	} 

	public Symbol getSymbol() {
		return symbol;
	}
}