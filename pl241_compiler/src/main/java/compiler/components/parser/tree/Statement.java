package compiler.components.parser.tree;

/**
 * statement = assignment | funcCall | ifStatement | whileStatement | returnStatement
 */ 
public class Statement extends TreeNode {

	protected StatementType statementType;

	public Statement(int lineNum, int charPos, StatementType statementType) {
		super(lineNum, charPos);
		this.statementType = statementType;
	}

	public StatementType getStatementType() {
		return statementType;
	}

	public static enum StatementType {
		IF(), ASSIGNMENT(), FUNCCALL(), WHILE(), RETURN();
	}
}