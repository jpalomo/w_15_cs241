package compiler.components.parser.tree;

/**
 * number = digit {digit}
 */
public class Number extends Factor {

	int value;

	public Number(int lineNum, int charPos, int value) {
		super(lineNum, charPos, FactorType.NUMBER);
		this.value = value;
	}

	public int getValue() {
		return value;
	} 
}
