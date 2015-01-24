package compiler.components.parser.tree;

/**
 * factor = designator | number | '(' expression ')' | funcCall
 */ 
public class Factor extends TreeNode {

	private FactorType type;
	private Symbol factorIdent;
	private String expression;

	public FactorType getType() {
		return type;
	}

	public Factor(int lineNum, int charPos, FactorType type) {
		super(lineNum, charPos);
		this.type = type;
	}

	public void setFactorIdent(Symbol symbol) {
		this.factorIdent = symbol;
	} 

	public Symbol getFactorIdent() {
		return factorIdent;
	}

	public static Factor combineFactors(Factor factor1, Factor factor2, String op){
		Number factor = null;
		Number num1 = (Number) factor1;
		Number num2 = (Number) factor2;
		if(op.equals("*")) {
			int result = num1.getValue() * num2.getValue();
			factor = new Number(factor2.getLineNum(), factor2.getCharPos(), result);
		}
		else if(op.equals("/")) {
			int result = num1.getValue() / num2.getValue();
			factor = new Number(num1.getLineNum(), num2.getCharPos(), result);
		}
		return factor;
	}

	public enum FactorType {
		NUMBER(), FUNCCALL(), IDENT(), ARRAY(), EXPRESSION();
	}
}