package compiler.components.intermeditate_rep;

import com.google.common.base.Preconditions;

public class Instruction {
	public int instructionNum;
	public String operand1;
	public String operator;
	public String operand2;

	private Instruction(int instructionNum, String operator, String operand1, String operand2) {
		this.instructionNum = instructionNum;
		this.operator = operator;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}

	public static InstructionBuilder builder(int instructionNum) {
		return new InstructionBuilder(instructionNum);
	}

	/**
	 * Returns a formatted string of the instruction: format = 1d:%6s %4s %4s
	 */
	public String toString() {
		String s = String.format("%-1d:%6s %4s %4s", instructionNum, operator, operand1, operand2);
		return s;
	}

	public static class InstructionBuilder {
		private int instructionNum;
		private String operand1;
		private String operator;
		private String operand2;

		private InstructionBuilder(int instructionNum){
			this.instructionNum = instructionNum;
		}

		public void operator(String operator){
			this.operator = operator;
		}

		public void op1(String operand1){
			this.operand1 = operand1;
		}

		public void op2(String operand2){
			this.operand2 = operand2;
		}

		public Instruction buildFixUpInstruction() {
			Preconditions.checkNotNull(operator, "operator must not be null");
			return new Instruction(instructionNum, operator, "", "");
		}

		public Instruction build() {
			Preconditions.checkNotNull(operator, "operator must not be null");
			Preconditions.checkNotNull(operand1, "operand1 must not be null");
			Preconditions.checkNotNull(operand2, "operand2 must not be null");
			return new Instruction(instructionNum, operator, operand1, operand2);
		}
	}
}