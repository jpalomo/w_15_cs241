package compiler.components.parser.tree;

import java.util.List;

/**
 * ifStatement = 'if' relation 'then' statSequence [ 'else' statSequence ] 'fi' 
 */ 
public class IfStatement extends Statement {

	Relation relation;
	List<Statement> ifBody;
	List<Statement> elseBody;

	public IfStatement(int lineNum, int charPos, Relation relation, List<Statement> ifBody, List<Statement> elseBody) {
		super(lineNum, charPos, StatementType.IF);
		this.statementType = StatementType.IF;
	}

	public Relation getRelation() {
		return relation;
	}

	public List<Statement> getIfBody() {
		return ifBody;
	}

	public List<Statement> getElseBody() {
		return elseBody;
	}
}