package compiler.components.parser.tree;

public class AbstractSyntaxTree {

	private Computation beginNode;

	public AbstractSyntaxTree(Computation beginNode) {
		this.beginNode = beginNode;
	}

	public Computation getComputationNode() {
		return beginNode;
	}

}
