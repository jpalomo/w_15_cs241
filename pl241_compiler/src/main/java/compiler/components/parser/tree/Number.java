package compiler.components.parser.tree;

/**
 * number = digit {digit}
 */
public class Number extends TreeNode {

	int numberValue;

	public Number(int lineNum, int charPos, int numberValue) {
		super(lineNum, charPos);
		this.numberValue = numberValue;
	}

	public int getNumberValue() {
		return numberValue;
	} 
}