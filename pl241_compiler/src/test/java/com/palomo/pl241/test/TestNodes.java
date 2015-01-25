package com.palomo.pl241.test;
import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import compiler.components.parser.Parser;
import compiler.components.parser.ParsingException;
import compiler.components.parser.tree.Computation;
import compiler.components.parser.tree.FuncDecl;
import compiler.components.parser.tree.Statement;
import compiler.components.parser.tree.VarDecl;

public class TestNodes extends TestCase {

	public void testComputationNotNull() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		Computation computation = parser.parse().getComputationNode();
		assertThat(computation).isNotNull();
	}

	public void testComputationFieldsAreNotNull() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		Computation computation = parser.parse().getComputationNode();
		assertThat(computation.getFuncDecl()).isNotNull();
		assertThat(computation.getVarDecl()).isNotNull();
		assertThat(computation.getStatSequence()).isNotNull();
	}

	public void testVarDecls001() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		List<VarDecl> varDecls= parser.parse().getComputationNode().getVarDecl();
		assertThat(varDecls.size()).isEqualTo(2);
		assertThat(varDecls.get(0).getIdent().getSymbol().toString()).isEqualTo("x");
		assertThat(varDecls.get(1).getIdent().getSymbol().toString()).isEqualTo("y");
	}

	public void testFuncDecls001() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		List<FuncDecl> funcDecls = parser.parse().getComputationNode().getFuncDecl();
		assertThat(funcDecls.size()).isEqualTo(0);
	}

	/**
	 * let x <- 51; 
     * let y <- 2 * x;
     * call OutputNum(y)
	 */
	public void testStatSequence001() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		List<Statement> statSeq = parser.parse().getComputationNode().getStatSequence();
		assertThat(statSeq.size()).isEqualTo(3);
		assertThat(statSeq.get(0).getAssignment().getDesignator().getIdent().getIdentAsString()).isEqualTo("x");
		assertThat(statSeq.get(0).getAssignment().getExpression().getTerm1().getNumberValue()).isEqualTo(51);
		assertThat(statSeq.get(1).getAssignment().getDesignator().getIdent().getIdentAsString()).isEqualTo("y");
		assertThat(statSeq.get(1).getAssignment().getExpression().getTerm1().getNumberValue()).isEqualTo(2);
		assertThat(statSeq.get(1).getAssignment().getExpression().getOp().toString()).isEqualTo("*");
		assertThat(statSeq.get(1).getAssignment().getExpression().getTerm2().getFactor1().getDesignator().getIdent().getIdentAsString()).isEqualTo("x");
	}

	/**
     * let y <- 2 * x;
	 */
	public void testTreeTest001() throws ParsingException {
		Parser parser = new Parser("src/test/resources/unit_tests/tree_test001.txt"); 
		List<Statement> statSeq = parser.parse().getComputationNode().getStatSequence();
		assertThat(statSeq.size()).isEqualTo(1);
		assertThat(statSeq.get(1).getAssignment().getDesignator().getIdent().getIdentAsString()).isEqualTo("y");
		assertThat(statSeq.get(1).getAssignment().getExpression().getTerm1().getNumberValue()).isEqualTo(2);
		assertThat(statSeq.get(1).getAssignment().getExpression().getOp().toString()).isEqualTo("*");
		assertThat(statSeq.get(1).getAssignment().getExpression().getTerm2().getFactor1().getDesignator().getIdent().getIdentAsString()).isEqualTo("x");
	}


	@Test(expected=ParsingException.class)
	public void testThrows() throws ParsingException {
	}
}
