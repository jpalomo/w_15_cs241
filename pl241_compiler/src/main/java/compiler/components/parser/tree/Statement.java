package compiler.components.parser.tree;

import com.google.common.base.Preconditions;

/**
 * statement = assignment | funcCall | ifStatement | whileStatement | returnStatement
 */ 
public class Statement extends TreeNode {

	private StatementType statementType;
	private Assignment assignment;
	private FuncCall funcCall;
	private IfStatement ifStatement;
	private WhileStatement whileStatement;
	private ReturnStatement returnStatement; 

	private Statement(int lineNum, int charPos) {
		super(lineNum, charPos);
	}
	
	public Assignment getAssignment() {
		return assignment;
	}

	public FuncCall getFuncCall() {
		return funcCall;
	}

	public IfStatement getIfStatement() {
		return ifStatement;
	}
	
	public WhileStatement getWhileStatement() {
		return whileStatement;
	}

	public ReturnStatement getReturnStatement() {
		return returnStatement;
	}

	public StatementType getStatementType() {
		return statementType;
	}

	public static StatementBuilder builder(int lineNum, int charPos) {
		return new StatementBuilder(lineNum, charPos);
	}

	public static class StatementBuilder {

		private int lineNum;
		private int charPos;

		private StatementType statementType;
		private Assignment assignment;
		private FuncCall funcCall;
		private IfStatement ifStatement;
		private WhileStatement whileStatement;
		private ReturnStatement returnStatement; 
		
		private StatementBuilder(int lineNum, int charPos) {
			this.lineNum = lineNum;
			this.charPos = charPos;
		}

		public StatementBuilder setFuncCall(FuncCall funcCall) {
			this.funcCall = funcCall;
			this.statementType = StatementType.FUNCCALL;
			return this;
		}

		public StatementBuilder setIfStatement(IfStatement ifStatement) {
			this.ifStatement = ifStatement;
			this.statementType = StatementType.IF;
			return this;
		}

		public StatementBuilder setWhileStatement(WhileStatement whileStatement) {
			this.whileStatement = whileStatement;
			this.statementType = StatementType.WHILE;
			return this;
		}

		public StatementBuilder setAssignment(Assignment assignment) {
			this.assignment = assignment;
			this.statementType = StatementType.ASSIGNMENT;
			return this;
		}

		public StatementBuilder setReturnStatement(ReturnStatement returnStatement) {
			this.returnStatement = returnStatement;
			this.statementType = StatementType.RETURN;
			return this;
		}

		public Statement build() {
			Preconditions.checkNotNull(statementType, "statement type must be set before building");
			
			Statement statement = new Statement(lineNum, charPos);
			statement.statementType = statementType;
			statement.assignment = assignment;
			statement.funcCall = funcCall;
			statement.ifStatement = ifStatement;
			statement.whileStatement = whileStatement;
			statement.returnStatement = returnStatement;
			return statement;
		}
	}

	public static enum StatementType {
		IF(), ASSIGNMENT(), FUNCCALL(), WHILE(), RETURN();
	}
}