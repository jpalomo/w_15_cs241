package compiler.components.parser.tree;

/**
 * relation = expression relOp expression
 */
public class Relation extends TreeNode {

	Expression left;
	Relop relOp;
	Expression right;

	public Relation(int lineNum, Symbol relOp, Expression left, Expression right) {
		super(lineNum);
		setRelOp(relOp);
		this.left = left;
		this.right = right;
	}

	public Relop getRelOp() {
		return relOp;
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	/**
	 * returns a string representation of the left expression, relational op,
	 * and right expression
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return sb.append(left.toString()).append(relOp.toString())
				.append(right.toString()).toString();
	}

	private void setRelOp(Symbol symbol) {
		if (symbol.toString().equals("==")) {
			this.relOp = Relop.EQUALS;
		} else if (symbol.toString().equals("<=")) {
			this.relOp = Relop.LESS_EQ;
		} else if (symbol.toString().equals("<")) {
			this.relOp = Relop.LESS;
		} else if (symbol.toString().equals(">=")) {
			this.relOp = Relop.GRTR_EQ;
		} else if (symbol.toString().equals(">")) {
			this.relOp = Relop.GRTR;
		} else if (symbol.toString().equals("!=")) {
			this.relOp = Relop.NOT_EQUALS;
		}
	}

	public static enum Relop {
		EQUALS("=="), NOT_EQUALS("!="), LESS("<"), LESS_EQ("<="), GRTR(">"), GRTR_EQ(
				">=");

		private String op;

		Relop(String op) {
			this.op = op;
		}

		/**
		 * returns the literal representation of the relational op
		 */
		public String toString() {
			return op;
		}
	}
}