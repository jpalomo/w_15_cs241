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
	private int numberValue; //the value if factor is a number
	private boolean isDesignator = false;
	private boolean isNumber = false;
	private boolean isExpression = false;
	private boolean isFuncCall = false;
	
	private String factorAsString;

	private Factor(int lineNum, int charPos){
		super(lineNum, charPos);
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

	public static FactorBuilder builder(int lineNum, int charPos) {
		return new FactorBuilder(lineNum, charPos);
	}

//	public static String createRegFactor(Factor factor1, Symbol op, Factor factor2) {
//		StringBuilder sb = new StringBuilder();
//		factor1.
//	}

	public enum FactorType {
		NUMBER(), FUNCCALL(), EXPRESSION(), DESIGNATOR();
	}

	public static class FactorBuilder {
		private int lineNum;
		private int charPos;
		private FactorType factorType;

		private Designator designator;
		private Number number;
		private Expression expression;
		private FuncCall funcCall;
		private int numberValue; //the value if factor is a number
		private boolean isDesignator = false;
		private boolean isNumber = false;
		private boolean isExpression = false;
		private boolean isFuncCall = false;
		private FactorBuilder(int lineNum, int charPos){
			this.lineNum = lineNum;
			this.charPos = charPos;
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
			Preconditions.checkNotNull(factorType, "statement type must be set before building");

			Factor factor = new Factor(lineNum, charPos);
			factor.designator = designator;
			factor.number = number;
			factor.expression = expression;
			factor.funcCall = funcCall;
			factor.factorType = factorType;
			factor.numberValue = numberValue;
			factor.isNumber = isNumber;
			factor.isDesignator = isDesignator;
			factor.isExpression = isExpression;
			factor.isFuncCall = isFuncCall;
			return factor; 
		}
	}
}