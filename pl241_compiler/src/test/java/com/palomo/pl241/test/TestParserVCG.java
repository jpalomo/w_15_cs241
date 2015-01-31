package com.palomo.pl241.test;
import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import org.junit.Test;

import compiler.components.parser.Parser;
import compiler.components.parser.ParserUtils;
import compiler.components.parser.ParsingException;
import compiler.components.parser.tree.Computation;
import compiler.components.parser.tree.FuncDecl;
import compiler.components.parser.tree.Statement;
import compiler.components.parser.tree.VarDecl;

import org.junit.Test;

import compiler.components.parser.ParserForVCG;
import compiler.components.parser.ParsingException;

public class TestParserVCG {

	@Test
	public void testSimpleVCG() throws ParsingException {
		ParserForVCG parser = new ParserForVCG("src/test/resources/unit_tests/cfg_simple_if.txt");
		parser.parse(); 
	}

	@Test
	public void testSymbolTable() throws ParsingException {
		ParserForVCG parser = new ParserForVCG("src/test/resources/unit_tests/cfg_simple_if.txt");
		parser.parse();
		assertThat(ParserUtils.symbolTable.size()).comparesEqualTo(3);
	}
}