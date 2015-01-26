package compiler.components.parser.tree;

public class Symbol {
	private String symbolVal;

	public Symbol(String symbolVal) {
		this.symbolVal = symbolVal;
	}

	/**
	 * @return returns the value of the symbol
	 */
	public String toString() {
		return symbolVal;
	}
}
