package compiler.components.parser.tree; 

//TODO create a toString method for this class
/**
 * term = factor { ('*' | '/') factor }
 */
public class Term extends TreeNode {
	private Factor factor1;
	private Factor factor2;
	private Symbol op;
	private int numberValue; //the value if term can be resolved to a number
	private boolean isNumber = false;

	private String nonNumberTermString;

	public Term(int lineNum, Factor factor1, Symbol op, Factor factor2) {
		super(lineNum);
		resolve(factor1, op, factor2);
        this.factor1 = factor1;
        this.factor2 = factor2;
        this.op = op;
	}

	public int getNumberValue() {
		return numberValue;
	}
	
	public Factor getFactor1() {
		return factor1;
	}
	
	public Symbol getOp() {
		return op;
	}

	public Factor getFactor2() {
		return factor2;
	}

	public boolean isNumber() {
		return isNumber;
	}

	private void resolve(Factor factor1, Symbol op, Factor factor2) {
		if(factor2 != null) {
			if(factor1.isNumber()) {
				if(factor2.isNumber()) {
					isNumber = true; /* if both numbers, we can combine into one factor*/
					if(op.toString().equals("*")){
						numberValue = factor1.getNumberValue() * factor2.getNumberValue();
					}
					else if(op.toString().equals("/")){
						numberValue = factor1.getNumberValue() * factor2.getNumberValue();
					}
				}
			}
		}else {
			if(factor1.isNumber()) {
				isNumber = true;
				this.numberValue = factor1.getNumberValue();
			} 	
		} 
	}
}