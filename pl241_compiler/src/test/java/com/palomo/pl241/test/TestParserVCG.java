package com.palomo.pl241.test;
import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import compiler.components.parser.ParserForVCG;
import compiler.components.parser.ParserUtils;
import compiler.components.parser.ParsingException;

public class TestParserVCG {

	@Test
	public void testSimpleIF_CFG() throws ParsingException {
		ParserForVCG parser = new ParserForVCG("src/test/resources/unit_tests/cfg_simple_if.txt"); 
		parser.parse(); 
		ParserUtils.printControlFlow(parser.beginBlock, "cfg_simple_if");
	}

	@Test
	public void test001_CFG() throws ParsingException {
		ParserForVCG parser = new ParserForVCG("src/test/resources/test001.txt"); 
		parser.parse(); 
		ParserUtils.printControlFlow(parser.beginBlock, "test001");
	} 


	@Test
	public void test007_CFG() throws ParsingException {
		ParserForVCG parser = new ParserForVCG("src/test/resources/test007.txt"); 
		parser.parse(); 
		ParserUtils.printControlFlow(parser.beginBlock, "test007");
	} 


	@Test
	public void testSymbolTable() throws ParsingException {
		ParserForVCG parser = new ParserForVCG("src/test/resources/unit_tests/cfg_simple_if.txt");
		parser.parse();
		assertThat(ParserUtils.symbolTable.size()).comparesEqualTo(6);
	}
}