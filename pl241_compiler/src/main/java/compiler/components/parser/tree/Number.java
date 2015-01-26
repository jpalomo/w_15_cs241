package compiler.components.parser.tree;

/**
 * number = digit {digit}
 */
public class Number extends TreeNode {

	int numberValue;

	public Number(int lineNum, int numberValue) {
		super(lineNum);
		this.numberValue = numberValue;
	}

	public int getNumberValue() {
		return numberValue;
	} 

	/**
	 * returns a string representation of the number
	 */
	public String toString() {
		return String.valueOf(numberValue);
	}
}