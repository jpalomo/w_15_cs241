package compiler.components.intermeditate_rep;

import java.util.LinkedList;
import java.util.List;

import compiler.components.parser.ParserUtils;

public class BasicBlock {
	private List<Instruction> instructions;
	private List<BasicBlock> dominatees;
	private List<BasicBlock> dominators;
	private List<BasicBlock> controlFlow;
	public Integer blockNumber;
	
	public BasicBlock() {
		instructions = new LinkedList<Instruction>();
		dominatees = new LinkedList<BasicBlock>();
		dominators = new LinkedList<BasicBlock>();
		controlFlow = new LinkedList<BasicBlock>();
		blockNumber = ParserUtils.getNewBlockNumber();
	}

	/**
	 * Adds an instruction to the list in sequential order
	 * @param instruction
	 */
	public void addInstruction(Instruction instruction) {
		instructions.add(instruction);
	}

	/**
	 * Add a basic block that dominates this block
	 * @param dominator
	 */
	public void addDominator(BasicBlock dominator) {
		dominators.add(dominator);
	}

	/**
	 * Add a basic block that this block dominates
	 * @param dominatee
	 */
	public void addDominatee(BasicBlock dominatee) {
		dominatees.add(dominatee);
	}

	public void addControlFlow(BasicBlock to){
		controlFlow.add(to);
	}

	public List<Instruction> getInstructions() {
		return instructions;
	}

	public List<BasicBlock> getDominatees() {
		return dominatees;
	}

	public List<BasicBlock> getDominators() {
		return dominators;
	}

	public List<BasicBlock> getControlFlow() {
		return controlFlow;
	}
}