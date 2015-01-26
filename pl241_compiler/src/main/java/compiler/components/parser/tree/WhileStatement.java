package compiler.components.parser.tree;

import java.util.List;

import com.google.common.base.Joiner;

/**
 * whileStatement = 'while' relation 'do' statSequence 'od'
 */ 
public class WhileStatement extends TreeNode {

	private Relation relation;
	private List<Statement> statSequence;

	public WhileStatement(int lineNum, Relation relation, List<Statement> statSequence) {
		super(lineNum);
		this.relation = relation;
		this.statSequence = statSequence;
	}

	public Relation getRelation() {
		return relation;
	}

	public List<Statement> getStatSequence() {
		return statSequence;
	}

	public String toString() {
		Joiner joiner = Joiner.on(" ");
		joiner.join(statSequence);

		StringBuilder sb = new StringBuilder("while ");
		sb.append(relation.toString());
		sb.append(" do ");
		sb.append(joiner.toString());
		sb.append(" od ");
		return sb.toString();
	}
}