package compiler.components.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import compiler.components.intermeditate_rep.BasicBlock;
import compiler.components.intermeditate_rep.Instruction;
import compiler.components.intermeditate_rep.Instruction.InstructionBuilder;
import compiler.components.intermeditate_rep.Result;
import compiler.components.intermeditate_rep.Result.ResultBuilder;
import compiler.components.intermeditate_rep.Result.ResultEnum;
import compiler.components.intermeditate_rep.VCGWriter;

public class ParserUtils {
	static Logger LOGGER = LoggerFactory.getLogger(ParserUtils.class);

	private InstructionBuilder iBuilder;

	/** Count to identify block uniquely */
	private static int blockCount;

	/** Symbol table of defined vairables */
	public Map<String, List<String>> symbolTable = new HashMap<String, List<String>>();

	/** Map holding the instruction number to the actual instruction */
	public Map<Integer, Instruction> programInstructions;

	/** The program instruction counter */
	public Integer programCounter;

	public ParserUtils() {
		blockCount = 0;
		programCounter = 1;

		programInstructions = new HashMap<Integer, Instruction>();

		// add the predefined functions to the symbol table
		symbolTable = new HashMap<String, List<String>>();
		symbolTable.put("InputNum", ImmutableList.<String>builder().add("read").build());
		symbolTable.put("OutputNum", ImmutableList.<String>builder().add("write").build());
		symbolTable.put("OutputNewLine", ImmutableList.<String>builder().add("writeNL").build());
	}

	public void updateSymbols(String symbol) throws ParsingException {
		List<String> symbols = symbolTable.get(symbol);
		if (symbols == null) {
			throw new ParsingException("symbol: " + symbol
					+ " was never declared.");
		}
		symbols.add(symbol + "_" + programCounter);
		symbolTable.put(symbol, symbols);
	}

	public void addSymbol(String symbol) {
		symbolTable.put(symbol, new LinkedList<String>());
	}

	public String getSymbolFromTable(String symbol) {
		List<String> symbols = symbolTable.get(symbol);
		if (symbols.size() == 0) {
			return symbol; // never used
		}
		return symbols.get(symbols.size() - 1); // return last declared
	}

	public Result emitAssignmentInstruction(Result r1, Result r2, BasicBlock bb) {

		InstructionBuilder assignmentBuilder = Instruction.builder(getNextInstructionNum()).operator("move");

		if (r2.type == ResultEnum.VARIABLE) {
			assignmentBuilder.op1(getSymbolFromTable(r2.varValue));
		} else if (r2.type == ResultEnum.REGISTER) {
			assignmentBuilder.op1("(" + r2.toString() + ")");
		} else {
			assignmentBuilder.op1(r2.constValue.toString());
		}

		assignmentBuilder.op2(getSymbolFromTable(r1.toString()));

		Instruction assignment = assignmentBuilder.build();
		programInstructions.put(assignment.instructionNum, assignment);
		bb.addInstruction(assignment.instructionNum);

		return Result.builder().type(ResultEnum.REGISTER).regNoValue(assignment.instructionNum).build();
	}

	public Result combineArithmetic(Result r1, String op, Result r2,
			BasicBlock bb) {
		ResultBuilder x = Result.builder();
		InstructionBuilder arithmeticBuilder;

		if (r1.type == ResultEnum.CONSTANT && r2.type == ResultEnum.CONSTANT) {
			x.type(ResultEnum.CONSTANT);
			if (op.equals("*")) {
				x.constValue(r1.constValue * r2.constValue);
			} else if (op.equals("/")) {
				x.constValue(r1.constValue / r2.constValue);
			} else if (op.equals("+")) {
				x.constValue(r1.constValue + r2.constValue);
			} else if (op.equals("-")) {
				x.constValue(r1.constValue - r2.constValue);
			}
		} else {
			String operator;
			if (op.equals("*") || op.equals("/")) {
				operator = op.equals("*") ? "mul" : "div";
			} else {
				operator = op.equals("+") ? "add" : "sub";
			}

			arithmeticBuilder = Instruction.builder(getNextInstructionNum()).operator(
					operator);
			x.type(ResultEnum.REGISTER).regNoValue(programCounter);

			// TODO why are these all the same?
			if (r1.type == ResultEnum.VARIABLE
					&& r2.type == ResultEnum.VARIABLE) {
				arithmeticBuilder.op1(r1.toString());
				arithmeticBuilder.op2(r2.toString());
			} else if (r1.type == ResultEnum.CONSTANT
					&& r2.type == ResultEnum.VARIABLE) {
				arithmeticBuilder.op1(r1.toString());
				arithmeticBuilder.op2(r2.toString());
			} else if (r1.type == ResultEnum.VARIABLE
					&& r2.type == ResultEnum.CONSTANT) {
				arithmeticBuilder.op1(r1.toString());
				arithmeticBuilder.op2(r2.toString());
			} else if (r1.type == ResultEnum.REGISTER) {
				arithmeticBuilder.op1("(" + r1.toString() + ")");
				arithmeticBuilder.op2(r2.toString());
			} else if (r2.type == ResultEnum.REGISTER) {
				arithmeticBuilder.op1(r1.toString());
				arithmeticBuilder.op2("(" + r2.toString() + ")");
			}

			Instruction arithmeticInst = arithmeticBuilder.build();
			programInstructions.put(arithmeticInst.instructionNum, arithmeticInst);
			bb.addInstruction(arithmeticInst.instructionNum);
		}
		return x.build();
	}

	public Result combineRelation(Result r1, String logicalOp, Result r2,
			BasicBlock bb) {
		Instruction conditionInst = Instruction
				.builder(getNextInstructionNum()).operator("cmp")
				.op1(r1.toString()).op2(r2.toString()).build();
		programInstructions.put(conditionInst.instructionNum, conditionInst);
		bb.addInstruction(conditionInst.instructionNum);

		Result result = Result.builder().type(ResultEnum.CONDITION)
				.regNoValue(programCounter).build();

		// create the branch instruction that will need to be fixed up
		InstructionBuilder branchInstBuilder = Instruction.builder(getNextInstructionNum());
		if (logicalOp.equals("<")) {
			branchInstBuilder.operator("bge");
		} 
		else if (logicalOp.equals(">")) {
			branchInstBuilder.operator("ble");
		}
		else if (logicalOp.equals("<=")) {
			branchInstBuilder.operator("bgt");
		}
		else if (logicalOp.equals(">=")) {
			branchInstBuilder.operator("blt");
		}
		else if (logicalOp.equals("==")) {
			branchInstBuilder.operator("bne");
		}
		else if (logicalOp.equals("!=")) {
			branchInstBuilder.operator("beq");
		}

		Instruction branchInstr = branchInstBuilder.build();
		programInstructions.put(branchInstr.instructionNum, branchInstr); // add the
		bb.addInstruction(branchInstr.instructionNum);

		return result;
	}

	/**
	 * Creates a new branch instruction in bb to branch to next generated instruction 
	 * @param bb
	 */
	public void createUnconditionBranch(BasicBlock bb, Integer location) {
		if(location == null){
			location = programCounter + 1;
		}
		Instruction branchInst = Instruction.builder(getNextInstructionNum()).operator("bra").op1("[" + location + "]").op2("").build();
		programInstructions.put(branchInst.instructionNum, branchInst);
		bb.addInstruction(branchInst.instructionNum);
		LOGGER.debug("Added instruction: " + branchInst.toString() + " to block number " + bb.blockNumber);
	}

	public void createFunctionCall(BasicBlock bb, Result funcName, Result funcParams){
		InstructionBuilder funcCallBuilder = Instruction.builder(getNextInstructionNum());
		//TODO fix this so that it can take in any kind of expression for funcParams
		funcCallBuilder.operator(symbolTable.get(funcName.varValue).get(0)).op1(funcParams.varValue).op2("");   

		Instruction funcCallInst = funcCallBuilder.build();
		programInstructions.put(funcCallInst.instructionNum, funcCallInst);
		bb.addInstruction(funcCallInst.instructionNum);
	}

	public void generatePhiInstructions(BasicBlock bb, BasicBlock joinBlock, boolean isLeft){
		for(Integer instructionNum: bb.getInstructions()) {
			Instruction instruction = programInstructions.get(instructionNum);
			String origVar = instruction.operand2.split("_")[0];
			if(instruction.operator.equals("move")) {
				if(isLeft) { //we always process the left side first
					Instruction phiInstr = Instruction.builder(getNextInstructionNum()).op1(instruction.operand2).op2(instruction.operand2).orignalPhiVar(instruction.operand2).buildPhi();
					programInstructions.put(phiInstr.instructionNum, phiInstr);
					joinBlock.addInstruction(phiInstr.instructionNum);
				}
				else{ //if were processing the right, either there is a phi or this right side value will fall through
					for(Integer joinInstructionNum : joinBlock.getInstructions()) {
						Instruction phiInstr = programInstructions.get(joinInstructionNum);
						if(phiInstr.isPhi &&  phiInstr.orignalPhiVar.equals(origVar)) {
							phiInstr.operand2 = instruction.operand2;
						}
						else {
//							phiInstr = Instruction.builder(getNextInstructionNum()).op1(instruction.operand2).op2(newValue).orignalPhiVar(instruction.operand2).buildPhi();
//							programInstructions.put(phiInstr.instructionNum, phiInstr);
//							joinBlock.addInstruction(phiInstr.instructionNum);
						}
					}
				}
			}
		}
	}

	public void conditionalJumpForward(BasicBlock condBB, String jumpDestination) {
//		List<Instruction> condBBInstructions = condBB.getInstructions();
//		Instruction inst = condBBInstructions
//				.get(condBBInstructions.size() - 1);
//		inst.operand1 = "[" + jumpDestination + "]";
	}

	public void addInstruction(Instruction instruction, BasicBlock bb){
		LOGGER.debug("Added instruction: " + iBuilder.build().toString());
		programInstructions.put(programCounter, iBuilder.build());
		bb.addInstruction(programCounter);
	}

	public void fixUp(Integer instructionNum) {
		Instruction fixUpInst = programInstructions.get(instructionNum);
		fixUpInst.operand1 = "[" + programCounter + "]";
		fixUpInst.operand2 = "";
	}

	public int getNextInstructionNum() {
		int current = programCounter++;
		return current;
	}

	public static int getNewBlockNumber() {
		return blockCount++;
	}

	public void printDominatorGraph(BasicBlock beginBlock, String fileName) {
		VCGWriter vcgwriter = new VCGWriter(fileName + "_dom.vcg", programInstructions);
		vcgwriter.emitBeginBasicBlock(beginBlock, true, false);
		vcgwriter.close();
	}

	public void printControlFlow(BasicBlock beginBlock, String fileName) {
		VCGWriter vcgwriter = new VCGWriter(fileName + "_cfg.vcg", programInstructions);
		vcgwriter.emitBeginBasicBlock(beginBlock, false, true);
		vcgwriter.close();
	}
}