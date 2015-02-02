package com.palomo.pl241.test;
import org.junit.Test;

import compiler.components.parser.Parser;
import compiler.components.parser.ParsingException;

public class TestParserVCG {

	@Test
	public void testSimpleIF_CFG() throws ParsingException {
		Parser parser = new Parser("src/test/resources/unit_tests/cfg_simple_if.txt"); 
		parser.parse(); 
		parser.printControlFlowToFile("cfg_simple_if");
	}

	@Test
	public void test001_CFG() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		parser.parse(); 
		parser.printControlFlowToFile("test001");
	} 

	@Test
	public void test007_CFG() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test007.txt"); 
		parser.parse(); 
		parser.printControlFlowToFile("test007");
	} 
}