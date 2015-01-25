package compiler.components.parser.tree;

import com.google.common.base.Preconditions;

/**
 * factor = designator | number | '(' expression ')' | funcCall
 */ 
public class Factor extends TreeNode {

	private FactorType factorType;

	private int numberValue; //the value if factor is a number
	private Designator designator;
	private Number number;
	private Expression expression;
	private FuncCall funcCall;

	private Factor(int lineNum, int charPos){
		super(lineNum, charPos);
	}

	public Designator getDesignator() {
		return designator;
	}
	
	private void setDesignator(Designator designator) {
		this.designator = designator;
	}	
	
	public Number getNumber() {
		return number;
	}
	
	private void setNumber(Number number) {
		this.number = number;
	}	
	
	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	public FuncCall getFuncCall() {
		return funcCall;
	}
	
	public void setFuncCall(FuncCall funcCall) {
		this.funcCall = funcCall;
	}
	
	public int getNumberValue() {
		return numberValue;
	}
	
	private void setNumberValue(int numberValue) {
		this.numberValue = numberValue;
	}

	public FactorType getFactorType() {
		return factorType;
	}

	private void setFactorType(FactorType factorType) {
		this.factorType = factorType;
	}

	public static FactorBuilder builder(int lineNum, int charPos) {
		return new FactorBuilder(lineNum, charPos);
	}

	public static Factor combineFactors(Factor factor1, Factor factor2, Symbol op){
		Number factor = null;
		Number num1 = (Number) factor1;
		Number num2 = (Number) factor2;
		if(op.toString().equals("*")) {
			int result = num1.getValue() * num2.getValue();
			factor = new Number(factor2.getLineNum(), factor2.getCharPos(), new Symbol(String.valueOf(result)));
		}
		else if(op.toString().equals("/")) {
			int result = num1.getValue() / num2.getValue();
			factor = new Number(num1.getLineNum(), num2.getCharPos(),  new Symbol(String.valueOf(result)));
		}
		return factor;
	}

	public enum FactorType {
		NUMBER(), FUNCCALL(), EXPRESSION(), DESIGNATOR();
	}

	public static class FactorBuilder {
		private int lineNum;
		private int charPos;
		private FactorType factorType;

		private int numberValue; //the value if factor is a number
		private Designator designator;
		private Number number;
		private Expression expression;
		private FuncCall funcCall;

		private FactorBuilder(int lineNum, int charPos){
			this.lineNum = lineNum;
			this.charPos = charPos;
		}

		public FactorBuilder setDesignator(Designator designator) {
			this.designator = designator;
			this.factorType = FactorType.DESIGNATOR;
			return this;
		}

		public FactorBuilder setNumber(Number number) {
			this.number = number;
			this.factorType = FactorType.NUMBER;
			this.numberValue = number.getNumberValue();
			return this;
		};

		public FactorBuilder setExpression(Expression expression) {
			this.expression = expression;
			this.factorType = FactorType.EXPRESSION;
			return this;
		}

		public FactorBuilder setFuncCall(FuncCall funcCall) {
			this.funcCall = funcCall;
			this.factorType = FactorType.FUNCCALL;
			return this;
		};

		public Factor build() {
			Preconditions.checkNotNull(factorType, "statement type must be set before building");

			Factor factor = new Factor(lineNum, charPos);
			factor.setDesignator(designator);
			factor.setNumber(number);
			factor.setExpression(expression);
			factor.setFuncCall(funcCall);
			factor.setFactorType(factorType);
			factor.setNumberValue(numberValue);
			return factor; 
		}
	}
}