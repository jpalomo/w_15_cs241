package compiler.components.parser.tree;

public abstract class TreeNode {

	int lineNum;

	TreeNode(int lineNum){
		this.lineNum = lineNum;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
}