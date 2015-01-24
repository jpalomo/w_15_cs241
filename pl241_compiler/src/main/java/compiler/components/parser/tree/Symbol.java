package compiler.components.parser.tree;

public class Symbol {
	private String symbol;
	private int lineNum;
	private int charPos;

	public Symbol(int lineNum, int charPos, String symbol) {
		this.lineNum = lineNum;
		this.charPos = charPos;
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}
}
