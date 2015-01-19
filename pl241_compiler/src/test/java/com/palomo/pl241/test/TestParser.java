package com.palomo.pl241.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import compiler.components.lex.Token;
import compiler.components.parser.Parser;

public class TestParser {

	@Test
	public void test001(){
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}

	@Test
	public void test006(){
		Parser parser = new Parser("src/test/resources/test006.txt"); 
		parser.parse();
		assertEquals(Token.Kind.EOF, parser.currentToken.kind); 
	}
}
