package compiler.components.parser.tree;

import compiler.components.parser.tree.TreeNode.ExpressionOp;

public class Expression {
	ExpressionOp type;
	int value;

	Expression leftExpression;
	Expression rightExpression; 

	public Expression(ExpressionOp operation, Expression left, Expression right) {
		switch(operation){
			case PLUS:
				value = left.getValue() + right.getValue();
				break;
			case MINUS:
				value = left.getValue() - right.getValue();
				break;
			case MULT:
				value = left.getValue() * right.getValue();
				break;
			case DIV:
				value = left.getValue() / right.getValue();
				break;
		} 
	}

	public int getValue() {
		return value;
	} 
}
