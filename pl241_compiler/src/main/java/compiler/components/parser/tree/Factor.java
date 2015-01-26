package compiler.components.parser.tree;

import com.google.common.base.Preconditions;

/**
 * factor = designator | number | '(' expression ')' | funcCall
 */
public class Factor extends TreeNode {

	private FactorType factorType;

	private Designator designator;
	private Number number;
	private Expression expression;
	private FuncCall funcCall;
	private int numberValue; // the value if factor is a number
	private boolean isDesignator = false;
	private boolean isNumber = false;
	private boolean isExpression = false;
	private boolean isFuncCall = false;


	private Factor(int lineNum, Designator designator, Number number,
			Expression expression, FuncCall funcCall, FactorType factorType,
			int numberValue, boolean isDesignator, boolean isNumber,
			boolean isExpression, boolean isFuncCall) {
		super(lineNum);
		this.designator = designator;
		this.expression = expression;
		this.funcCall = funcCall;
		this.factorType = factorType;
		this.number = number;
		this.numberValue = numberValue;
		this.isDesignator = isDesignator;
		this.isNumber = isNumber;
		this.isExpression = isExpression;
		this.isFuncCall = isFuncCall;
	}

	public Designator getDesignator() {
		return designator;
	}

	public Number getNumber() {
		return number;
	}

	public Expression getExpression() {
		return expression;
	}

	public FuncCall getFuncCall() {
		return funcCall;
	}

	public int getNumberValue() {
		return numberValue;
	}

	public boolean isNumber() {
		return isNumber;
	}

	public boolean isDesignator() {
		return isDesignator;
	}

	public boolean isExpression() {
		return isExpression;
	}

	public boolean isFuncCall() {
		return isFuncCall;
	}

	public FactorType getFactorType() {
		return factorType;
	}

	public static FactorBuilder builder(int lineNum) {
		return new FactorBuilder(lineNum);
	}

	public String toString() {
		String factorAsString = "factor could not be set as a string";

		if(isNumber) {
			 factorAsString = String.valueOf(numberValue);
		}
		else if(isDesignator){
			factorAsString = designator.toString();
		}
		else if(isExpression) {
			factorAsString = expression.toString(); //TODO create toString method in expression
		}
		else if(isFuncCall) {
			factorAsString = funcCall.toString();
		}
		return factorAsString;
	}

	// public static String createRegFactor(Factor factor1, Symbol op, Factor
	// factor2) {
	// StringBuilder sb = new StringBuilder();
	// factor1.
	// }

	public enum FactorType {
		NUMBER(), FUNCCALL(), EXPRESSION(), DESIGNATOR();
	}

	public static class FactorBuilder {
		private int lineNum;
		private FactorType factorType;

		private Designator designator;
		private Number number;
		private Expression expression;
		private FuncCall funcCall;
		private int numberValue; // the value if factor is a number
		private boolean isDesignator = false;
		private boolean isNumber = false;
		private boolean isExpression = false;
		private boolean isFuncCall = false;

		private FactorBuilder(int lineNum) {
			this.lineNum = lineNum;
		}

		public FactorBuilder setDesignator(Designator designator) {
			this.designator = designator;
			this.factorType = FactorType.DESIGNATOR;
			this.isDesignator = true;
			return this;
		}

		public FactorBuilder setNumber(Number number) {
			this.number = number;
			this.factorType = FactorType.NUMBER;
			this.numberValue = number.getNumberValue();
			this.isNumber = true;
			return this;
		};

		public FactorBuilder setExpression(Expression expression) {
			this.expression = expression;
			this.factorType = FactorType.EXPRESSION;
			this.isExpression = true;
			return this;
		}

		public FactorBuilder setFuncCall(FuncCall funcCall) {
			this.funcCall = funcCall;
			this.factorType = FactorType.FUNCCALL;
			this.isFuncCall = true;
			return this;
		};

		public Factor build() {
			Preconditions.checkNotNull(factorType,
					"statement type must be set before building");
			return new Factor(lineNum, designator, number, expression,
					funcCall, factorType, numberValue, isNumber, isDesignator,
					isExpression, isFuncCall);
		}
	}
}