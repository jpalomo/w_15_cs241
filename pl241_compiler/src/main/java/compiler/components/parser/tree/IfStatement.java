package compiler.components.parser.tree;

import java.util.List;

/**
 * ifStatement = 'if' relation 'then' statSequence [ 'else' statSequence ] 'fi' 
 */ 
public class IfStatement extends TreeNode {

	Relation relation;
	List<Statement> ifBody;
	List<Statement> elseBody;

	public IfStatement(int lineNum, int charPos, Relation relation, List<Statement> ifBody, List<Statement> elseBody) {
		super(lineNum, charPos);
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