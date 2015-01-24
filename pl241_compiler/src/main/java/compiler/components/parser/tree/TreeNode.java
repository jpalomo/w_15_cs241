package compiler.components.parser.tree;

public abstract class TreeNode {

	int lineNum;
	int charPos;

	TreeNode(int lineNum, int charPos){
		this.lineNum = lineNum;
		this.charPos = charPos;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public int getCharPos() {
		return charPos;
	}

	public void setCharPos(int charPos) {
		this.charPos = charPos;
	} 
}