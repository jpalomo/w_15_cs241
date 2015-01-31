package compiler.components.parser.tree;

public class Ident extends TreeNode {

	private Symbol symbol;

	public Ident(int lineNum, Symbol symbol) {
		super(lineNum);
		this.symbol = symbol;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}

	/**
	 * returns the value of the symbol as a string
	 */
	public String toString() {
		return symbol.toString();
	}
}