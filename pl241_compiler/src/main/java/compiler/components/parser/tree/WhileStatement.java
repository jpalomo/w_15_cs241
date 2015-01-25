package compiler.components.parser.tree;

import java.util.List;

/**
 * whileStatement = 'while' relation 'do' statSequence 'od'
 */ 
public class WhileStatement extends TreeNode {

	private Relation relation;
	private List<Statement> statSequence;

	public WhileStatement(int lineNum, int charPos, Relation relation, List<Statement> statSequence) {
		super(lineNum, charPos);
		this.relation = relation;
		this.statSequence = statSequence;
	}

	public Relation getRelation() {
		return relation;
	}

	public List<Statement> getStatSequence() {
		return statSequence;
	}
}
