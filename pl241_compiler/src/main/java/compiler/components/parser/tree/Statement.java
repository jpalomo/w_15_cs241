package compiler.components.parser.tree;

import com.google.common.base.Preconditions;

//TODO create a toString method for this class
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

	private Statement(int lineNum, StatementType statementType, Assignment assignment, FuncCall funcCall, IfStatement ifStatement, WhileStatement whileStatement, ReturnStatement returnStatement) {
		super(lineNum);
		this.statementType = statementType;
		this.assignment = assignment;
		this.funcCall = funcCall;
		this.ifStatement = ifStatement;
		this.whileStatement = whileStatement;
		this.returnStatement = returnStatement;
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

	public static StatementBuilder builder(int lineNum) {
		return new StatementBuilder(lineNum);
	}

	public static class StatementBuilder {

		private int lineNum;

		private StatementType statementType;
		private Assignment assignment;
		private FuncCall funcCall;
		private IfStatement ifStatement;
		private WhileStatement whileStatement;
		private ReturnStatement returnStatement; 
		
		private StatementBuilder(int lineNum) {
			this.lineNum = lineNum;
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
			return new Statement(lineNum, statementType, assignment, funcCall, ifStatement, whileStatement, returnStatement);
		}
	}

	public static enum StatementType {
		IF(), ASSIGNMENT(), FUNCCALL(), WHILE(), RETURN();
	}
}