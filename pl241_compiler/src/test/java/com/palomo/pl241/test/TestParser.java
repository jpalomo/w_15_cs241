package com.palomo.pl241.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import compiler.components.lex.Token;
import compiler.components.parser.Parser;

public class TestParser {

	@Test
	public void testExpect(){
		Parser parser = new Parser("src/test/resources/unit_tests/token_main.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test001(){
		Parser parser = new Parser("src/test/resources/unit_tests/test001_no_comment.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
}
