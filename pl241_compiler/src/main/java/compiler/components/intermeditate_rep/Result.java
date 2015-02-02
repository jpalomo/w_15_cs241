package compiler.components.intermeditate_rep;

import com.google.common.base.Preconditions;


public class Result {
	public ResultEnum type = null;
	public Integer constValue = null;
	public Integer regNoValue = null;
	public Integer fixUp = null;
	public String varValue = null;
	public String conditionValue = null;  //TODO what will this get set to?

	public enum ResultEnum {
		CONSTANT(), REGISTER(), VARIABLE(), CONDITION();
	} 

	private Result(ResultEnum type, Integer constValue, Integer regNoValue, Integer fixUp, String varValue, String conditionValue){
		this.type = type; 
		this.constValue = constValue;
		this.regNoValue = regNoValue;
		this.fixUp = fixUp;
		this.varValue = varValue;
		this.conditionValue = conditionValue;
	}

	public static ResultBuilder builder() {
		return new ResultBuilder();
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

	public static class ResultBuilder {
		private ResultEnum type = null;
		private Integer constValue = null;
		private Integer regNoValue = null;
		private Integer fixUp = null;
		private String varValue = null;
		private String conditionValue = null;  //TODO what will this get set to?

		private ResultBuilder() {
		}

		public ResultBuilder type(ResultEnum type) {
			this.type = type;
			return this;
		}

		public ResultBuilder constValue(int constValue) {
			this.constValue = constValue;
			return this;
		}

		public ResultBuilder regNoValue(int regNoValue) {
			this.regNoValue = regNoValue;
			return this;
		}

		public ResultBuilder fixUp(int fixUp) {
			this.fixUp = fixUp;
			return this;
		}

		public ResultBuilder varValue(String varValue) {
			this.varValue = varValue;
			return this;
		}
		
		public ResultBuilder conditionValue(String conditionValue) {
			this.conditionValue = conditionValue;
			return this;
		}

		public Result build() {
			Preconditions.checkNotNull(type, "type cannot be null");
			return new Result(type, constValue, regNoValue, fixUp, varValue, conditionValue);
		}
		
	}
}
