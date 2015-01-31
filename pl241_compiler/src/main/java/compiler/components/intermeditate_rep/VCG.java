package compiler.components.intermeditate_rep;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Each method is responsible for creating a new line.
 * 
 * @author Palomo
 *
 */
public class VCG {
	public static final String fileName = "cfg.vcg";

	private PrintWriter writer;
	private static VCG vcg = null;

	//only want one instance of this
	public static VCG getInstance() {
		if (vcg == null) {
			vcg = new VCG();
		}
		return vcg;
	}

	public void emitBeginBasicBlock(BasicBlock bb) {
		writer.println();
		writer.println("node: {");
		writer.println("title: \"" + bb.blockNumber + "\"");
		writer.print("label: \"" + bb.blockNumber + "[");
		writeBBInstructions(bb);

        emitExitBasicBlock();
		//Create all the dominator links
		for(BasicBlock dominatee: bb.getDominatees()){
			writer.println();
			writer.println("edge: { sourcename: " + "\"" + bb.blockNumber + "\"");
			writer.println("targetname: " + "\"" +  dominatee.blockNumber + "\"" );
			writer.println("color: blue");
			writer.println("}");
		}

		for(BasicBlock dominatee: bb.getDominatees()){
			emitBeginBasicBlock(dominatee);
		}
	}

	private VCG() {
		try {
			writer = new PrintWriter(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		preamble();
	}

	private void preamble() {
		writer.println("graph: { title: \"Control Flow Graph\"");
		writer.println("layoutalgorithm: dfs");
		writer.println("manhattan_edges: yes");
		writer.print("smanhattan_edges: yes");
	}

	private void writeBBInstructions(BasicBlock bb) {

		for(Instruction inst: bb.getInstructions()) {
			writer.println();
			writer.print(inst.toString());
		}
		writer.write("  ]\"");
	}

	private void emitExitBasicBlock() {
		writer.println();
		writer.print("}");
	}

	public void endAndClose() {
		writer.println();
		writer.print("}");
		writer.close();
	}
}