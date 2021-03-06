package compiler.components.parser.tree;


/**
 * expression = term  { ('+' | '-') term }
 */ 
public class Expression extends TreeNode {

	private Term term1;
	private Symbol op;
	private Term term2;
	private boolean isNumber = false;
	private int numberValue; //the value if expression can be resolved to a number

	private Expression(int lineNum, Term term1, Symbol op, Term term2) {
		super(lineNum);
		resolve(term1, op, term2);
	}

	public Term getTerm1() {
		return term1;
	}

	public Symbol getOp() {
		return op;
	}

	public Term getTerm2() {
		return term2;
	}

	public int getNumberValue() {
		return numberValue;
	}

	public boolean isNumber() {
		return isNumber;
	}
	
	private void resolve(Term term1, Symbol op, Term term2) {
		if(term2 != null && term1.isNumber() && term2.isNumber()) {
			if(op.toString().equals("+")) {
				numberValue = term1.getNumberValue() + term2.getNumberValue();
			}
			else if(op.toString().equals("-")) {
				numberValue = term1.getNumberValue() - term2.getNumberValue();
			}
		}
		else {
			if (term1.isNumber()) {
				isNumber = true;
				this.numberValue = term1.getNumberValue();
			}
			this.term1 = term1;
			this.term2 = term2;
			this.op = op;
		}
	}

	/**
	 * returns a string representation of the term1, op, and term2
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(term1.toString());
		sb.append(" ");
		sb.append(op.toString());
		sb.append(" ");
		sb.append(term2.toString());
		return sb.toString(); 
	}

	public static ExpressionBuilder builder(int lineNum) {
		return new ExpressionBuilder(lineNum);
	}

	public static class ExpressionBuilder {
		private int lineNum;

		private Term term1;
		private Symbol op;
		private Term term2;

		private ExpressionBuilder(int lineNum){
			this.lineNum = lineNum;
		}

		public ExpressionBuilder setTerm1(Term term1) {
			this.term1 = term1;
			return this;
		}

		public ExpressionBuilder setTerm2(Term term2) {
			this.term2 = term2;
			return this;
		} 

		public ExpressionBuilder setOp(Symbol op) {
			this.op = op;
			return this;
		}

		public Expression build() {
			Expression expression = new Expression(lineNum, term1, op, term2);
			return expression; 
		}
	}
} 