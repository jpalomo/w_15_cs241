package compiler.components.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import compiler.components.intermeditate_rep.BasicBlock;
import compiler.components.intermeditate_rep.Instruction;
import compiler.components.intermeditate_rep.Instruction.InstructionBuilder;
import compiler.components.intermeditate_rep.VCG;
import compiler.components.parser.Result.ResultEnum;

public class ParserUtils {
	private static InstructionBuilder iBuilder;
	private static VCG cfg = VCG.getInstance();
	public static int count = 0;
	public static Map<String, List<String>> symbolTable = new HashMap<String, List<String>>();

	static {
		cfg = VCG.getInstance();
		count = 0;
		symbolTable = new HashMap<String, List<String>>();
	}

	private ParserUtils() {
		//dont want to instantiate, just a static class
	}

	public static void updateSymbols(String symbol) throws ParsingException {
		List<String> symbols = symbolTable.get(symbol);
		if (symbols == null) {
			throw new ParsingException("symbol: " + symbol + " was never declared.");
		}
		symbols.add(symbol + "_" + count);
		symbolTable.put(symbol, symbols);
	}

	public static void addSymbol(String symbol){
		symbolTable.put(symbol, new LinkedList<String>());
	}

	public static String getSymbolFromTable(String symbol) {
		List<String> symbols = symbolTable.get(symbol);
		if (symbols.size() == 0) {
			return symbol; // never used 
		}
		return symbols.get(symbols.size() - 1);  //return last declared
	}

	public static Result emitAssignmentInstruction(Result r1, Result r2, BasicBlock basicBlock) {
		iBuilder = Instruction.builder(getNewNumber());
		iBuilder.operator("move");
		if (r2.type == ResultEnum.VARIABLE) {
			iBuilder.op1(getSymbolFromTable(r2.varValue));
		} 
		else if (r2.type == ResultEnum.REGISTER) {
			iBuilder.op1("(" + r2.toString() +")");
		}
		else {
			iBuilder.op1(r2.constValue.toString());
		}
		iBuilder.op2(getSymbolFromTable(r1.toString()));

		Instruction inst = iBuilder.build();
		basicBlock.addInstruction(inst);

		Result x = new Result(); 
		x.regNoValue = inst.instructionNum;
		x.type = ResultEnum.REGISTER;
		
		return x;
	} 

	public static Result combineArithmetic(Result r1, String op, Result r2, BasicBlock bb) {
		Result x = new Result();

		if (r1.type == ResultEnum.CONSTANT && r2.type == ResultEnum.CONSTANT) {
			x.type = ResultEnum.CONSTANT;
			if (op.equals("*")) {
				x.constValue = r1.constValue * r2.constValue;
			}
			else if(op.equals("/")) {
				x.constValue = r1.constValue / r2.constValue;
			}
			else if(op.equals("+")) {
				x.constValue = r1.constValue + r2.constValue;
			}
			else if(op.equals("-")) {
				x.constValue = r1.constValue - r2.constValue;
			}
		} else {
			String operator; 
			if(op.equals("*") || op.equals("/")) {
				operator = op.equals("*") ? "mul" : "div";
			}
			else {
				operator = op.equals("+") ? "add" : "sub";
			} 

			x.type = ResultEnum.REGISTER;
			x.regNoValue = count;  //get the instruction number before we increment

			iBuilder = Instruction.builder(getNewNumber());
			iBuilder.operator(operator); 

			if (r1.type == ResultEnum.VARIABLE && r2.type == ResultEnum.VARIABLE) {
				iBuilder.op1(r1.toString());
				iBuilder.op2(r2.toString());
			} 
			else if (r1.type == ResultEnum.CONSTANT && r2.type == ResultEnum.VARIABLE) {
				iBuilder.op1(r1.toString());
				iBuilder.op2(r2.toString());
			}
			else if (r1.type == ResultEnum.VARIABLE && r2.type == ResultEnum.CONSTANT) {
				iBuilder.op1(r1.toString());
				iBuilder.op2(r2.toString());
			}
			else if (r1.type == ResultEnum.REGISTER) {
				iBuilder.op1("(" + r1.toString() + ")");
				iBuilder.op2(r2.toString());
			}
			else if(r2.type == ResultEnum.REGISTER){
				iBuilder.op1(r1.toString());
				iBuilder.op2("(" + r2.toString() + ")");
			}
			Instruction instruction = iBuilder.build();
			bb.addInstruction(instruction);
		}
		return x;
	}

	public static Result combineRelation(Result r1, String logicalOp, Result r2, BasicBlock bb) {
		Result result = new Result();
		result.regNoValue = count;
		result.type = ResultEnum.CONDITION;
		iBuilder = Instruction.builder(getNewNumber());

		iBuilder.operator("cmp");
		iBuilder.op1(r1.toString());
		iBuilder.op2(r2.toString());
		bb.addInstruction(iBuilder.build());;

		//create the branch instruction
		iBuilder = Instruction.builder(getNewNumber());
		if(logicalOp.equals("<")){
			iBuilder.operator("bge");
			Instruction inst = iBuilder.buildFixUpInstruction();
			bb.addInstruction(inst);
			result.fixUp.add(inst);
		}

		return result;
	}

	public static Instruction createUnconditionBranch(BasicBlock bb, String branchDestination) {
		iBuilder = Instruction.builder(getNewNumber());
		iBuilder.operator("bra");
		iBuilder.op1("[" + branchDestination + "]");
		Instruction inst = iBuilder.buildFixUpInstruction();
		bb.addInstruction(inst);
		return inst; 
	}

	public static void conditionalJumpForward(BasicBlock condBB, String jumpDestination) {
		List<Instruction> condBBInstructions = condBB.getInstructions();
		Instruction inst = condBBInstructions.get(condBBInstructions.size() - 1);
		inst.operand1 = "[" + jumpDestination + "]"; 
	}

	public static BasicBlock createBBWithDomRelationship(BasicBlock currentBlock) {
		BasicBlock newBlock = new BasicBlock();
		currentBlock.addDominatee(newBlock);
		newBlock.addDominator(currentBlock);
		return newBlock;
	}

	public static void end() {
		cfg.endAndClose();
	}

	public static int getNewNumber() {
		return count++;
	}

	public static void writeOutBlocks(BasicBlock beginBlock) { 
		cfg.emitBeginBasicBlock(beginBlock);
	}
}
