package compiler.components.parser.tree;

import java.util.List;

import com.google.common.base.Joiner;

/**
 * ifStatement = 'if' relation 'then' statSequence [ 'else' statSequence ] 'fi' 
 */ 
public class IfStatement extends TreeNode {

	Relation relation;
	List<Statement> ifBody;
	List<Statement> elseBody;

	public IfStatement(int lineNum, Relation relation, List<Statement> ifBody, List<Statement> elseBody) {
		super(lineNum);
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

	/**
	 * returns a string representation of the relation, ifBody statements, and elseBody statements
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("if ");
		Joiner joiner = Joiner.on(" ");
		joiner.join(ifBody);
		joiner.join(elseBody);
		return sb.append(joiner.join(ifBody)).append(" ").append(joiner.join(elseBody)).append(" fi").toString();
		
	}
}