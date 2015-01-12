package com.palomo.pl241.test;

import static org.junit.Assert.*;

import org.junit.Test;

import compiler.components.lex.Scanner;
import compiler.components.lex.Token.Kind;


public class TestScanner
{
	@Test
	public void testDistinguishKeywordFromIdentifier() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/keywordfromidentifier.txt"); 
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.MAIN, scanner.token.getKind());
		assertEquals(Kind.MAIN.getValue(), tokenVal); 
		tokenVal = scanner.nextToken();
		assertEquals(Kind.IDENTIFIER, scanner.token.getKind());
		assertEquals(Kind.IDENTIFIER.getValue(), tokenVal); 
		assertEquals(scanner.token.getLexeme(), "main1"); 
	}
	
	@Test
	public void testMainToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_main.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.MAIN, scanner.token.getKind());
		assertEquals(Kind.MAIN.getValue(), tokenVal); 
	}

	@Test
	public void testVarToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_var.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.VAR, scanner.token.getKind());
		assertEquals(Kind.VAR.getValue(), tokenVal); 
	}

	@Test
	public void testArrayToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_array.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.ARRAY, scanner.token.getKind());
		assertEquals(Kind.ARRAY.getValue(), tokenVal); 
	}

	@Test
	public void testFunctionToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_function.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.FUNCTION, scanner.token.getKind());
		assertEquals(Kind.FUNCTION.getValue(), tokenVal); 
	}

	@Test
	public void testProcedureToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_procedure.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.PROCEDURE, scanner.token.getKind());
		assertEquals(Kind.PROCEDURE.getValue(), tokenVal); 
	}

	@Test
	public void testLetToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_let.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.LET, scanner.token.getKind());
		assertEquals(Kind.LET.getValue(), tokenVal); 
	}

	@Test
	public void testThenToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_then.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.THEN, scanner.token.getKind());
		assertEquals(Kind.THEN.getValue(), tokenVal); 
	}

	@Test
	public void testCallToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_call.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.CALL, scanner.token.getKind());
		assertEquals(Kind.CALL.getValue(), tokenVal); 
	}

	@Test
	public void testDoToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_do.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.DO, scanner.token.getKind());
		assertEquals(Kind.DO.getValue(), tokenVal); 
	}

	@Test
	public void testOdToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_od.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.OD, scanner.token.getKind());
		assertEquals(Kind.OD.getValue(), tokenVal); 
	}

	@Test
	public void testIfToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_if.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.IF, scanner.token.getKind());
		assertEquals(Kind.IF.getValue(), tokenVal); 
	}

	@Test
	public void testFiToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_fi.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.FI, scanner.token.getKind());
		assertEquals(Kind.FI.getValue(), tokenVal); 
	}

	@Test
	public void testElseToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_else.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.ELSE, scanner.token.getKind());
		assertEquals(Kind.ELSE.getValue(), tokenVal); 
	}

	@Test
	public void testWhileToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_while.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.WHILE, scanner.token.getKind());
		assertEquals(Kind.WHILE.getValue(), tokenVal); 
	}

	@Test
	public void testReturnToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_return.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.RETURN, scanner.token.getKind());
		assertEquals(Kind.RETURN.getValue(), tokenVal); 
	}

	@Test
	public void testTimesToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_times.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.TIMES, scanner.token.getKind());
		assertEquals(Kind.TIMES.getValue(), tokenVal); 
	}

	@Test
	public void testDivToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_div.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.DIV, scanner.token.getKind());
		assertEquals(Kind.DIV.getValue(), tokenVal); 
	}

	@Test
	public void testPlusToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_plus.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.PLUS, scanner.token.getKind());
		assertEquals(Kind.PLUS.getValue(), tokenVal); 
	}

	@Test
	public void testMinusToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_minus.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.MINUS, scanner.token.getKind());
		assertEquals(Kind.MINUS.getValue(), tokenVal); 
	}

	@Test
	public void testEqualToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_equal.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.EQL, scanner.token.getKind());
		assertEquals(Kind.EQL.getValue(), tokenVal); 
	}

	@Test
	public void testNotEqualToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_nequal.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.NEQ, scanner.token.getKind());
		assertEquals(Kind.NEQ.getValue(), tokenVal); 
	}

	public void testLessToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_less.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.LESS, scanner.token.getKind());
		assertEquals(Kind.LESS.getValue(), tokenVal); 
	}

	@Test
	public void testLessEqualToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_lesseq.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.LEQ, scanner.token.getKind());
		assertEquals(Kind.LEQ.getValue(), tokenVal); 
	}

	@Test
	public void testGreaterToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_greater.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.GRTR, scanner.token.getKind());
		assertEquals(Kind.GRTR.getValue(), tokenVal); 
	}

	@Test
	public void testGreaterEqualToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_greatereq.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.GEQ, scanner.token.getKind());
		assertEquals(Kind.GEQ.getValue(), tokenVal); 
	}

	@Test
	public void testPeriodToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_period.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.PERIOD, scanner.token.getKind());
		assertEquals(Kind.PERIOD.getValue(), tokenVal); 
	}

	@Test
	public void testCommanToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_comma.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.COMMA, scanner.token.getKind());
		assertEquals(Kind.COMMA.getValue(), tokenVal); 
	}

	@Test
	public void testOpenBrackToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_openbrk.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.OPN_BRACK, scanner.token.getKind());
		assertEquals(Kind.OPN_BRACK.getValue(), tokenVal); 
	}

	@Test
	public void testCloseBrackToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_closebrk.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.CLS_BRACK, scanner.token.getKind());
		assertEquals(Kind.CLS_BRACK.getValue(), tokenVal); 
	}

	@Test
	public void testOpenParenToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_openparen.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.OPN_PAREN, scanner.token.getKind());
		assertEquals(Kind.OPN_PAREN.getValue(), tokenVal); 
	}

	@Test
	public void testCloseParenToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_closeparen.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.CLS_PAREN, scanner.token.getKind());
		assertEquals(Kind.CLS_PAREN.getValue(), tokenVal); 
	}

	@Test
	public void testBecomesToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_becomes.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.BECOMES, scanner.token.getKind());
		assertEquals(Kind.BECOMES.getValue(), tokenVal); 
	}

	@Test
	public void testSemiColToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_semicol.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.SEMI_COL, scanner.token.getKind());
		assertEquals(Kind.SEMI_COL.getValue(), tokenVal); 
	}

	@Test
	public void testBeginToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_begin.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.BEGIN, scanner.token.getKind());
		assertEquals(Kind.BEGIN.getValue(), tokenVal); 
	}

	@Test
	public void testEndToken() {
		Scanner scanner = new Scanner("src/test/resources/unit_tests/token_end.txt");
		int tokenVal = scanner.nextToken();
		assertEquals(Kind.END, scanner.token.getKind());
		assertEquals(Kind.END.getValue(), tokenVal); 
	}

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyFileLocation() {
    	new Scanner("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullFileLocation() {
    	new Scanner(null);
    }
    
    @Test
    public void testErrorToken() {
    	Scanner scanner = new Scanner(String.valueOf(hashCode()));
    	int tokenVal = scanner.nextToken();
		assertEquals(Kind.ERROR, scanner.token.getKind());
		assertEquals(Kind.ERROR.getValue(), tokenVal); 
    }

    @Test
    public void testEOFToken() {
    	Scanner scanner = new Scanner("src/test/resources/unit_tests/empty.txt");
    	int tokenVal = scanner.nextToken();
		assertEquals(Kind.EOF, scanner.token.getKind());
		assertEquals(Kind.EOF.getValue(), tokenVal); 
    }

    @Test
    public void testBadInput() {
    	Scanner scanner = new Scanner("src/test/resources/unit_tests/A.txt");
    	int tokenVal = scanner.nextToken();
		assertEquals(Kind.ERROR, scanner.token.getKind());
		assertEquals(Kind.ERROR.getValue(), tokenVal); 
		assertEquals("A", scanner.token.getLexeme()); 
    }

    @Test
    public void testNumberToken() {
    	Scanner scanner = new Scanner("src/test/resources/unit_tests/12345.txt");
    	int tokenVal = scanner.nextToken();
		assertEquals(Kind.NUMBER, scanner.token.getKind());
		assertEquals(Kind.NUMBER.getValue(), tokenVal); 
		assertEquals("12345", scanner.token.getLexeme()); 
    }
}
