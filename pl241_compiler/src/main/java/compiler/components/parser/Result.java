package compiler.components.parser;

import java.util.LinkedList;
import java.util.List;

import compiler.components.intermeditate_rep.Instruction;

public class Result {
	public ResultEnum type;

	public Integer constValue = null;
	public Integer regNoValue = null;
	public String varValue = null;
	public String conditionValue = null;  //TODO what will this get set to?
	public List<Instruction> fixUp;

	public enum ResultEnum {
		CONSTANT(), REGISTER(), VARIABLE(), CONDITION();
	} 

	public Result() {
		fixUp = new LinkedList<Instruction>();
	}

	public String toString(){
		if(constValue != null){
			return constValue.toString();
		}
		if(regNoValue != null) {
			return regNoValue.toString();
		}
		return varValue;
	}
}
