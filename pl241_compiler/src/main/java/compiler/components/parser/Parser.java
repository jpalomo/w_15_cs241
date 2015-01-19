package compiler.components.parser;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.lex.Scanner;
import compiler.components.lex.Token;
import compiler.components.lex.Token.Kind;

/**
 * Implementation of a top-down recursive descent parser.
 *
 * @author John Palomo, 60206611
 */
public class Parser {

	private Scanner scanner;
	public Token currentToken;

	static Logger LOGGER = LoggerFactory.getLogger(Parser.class);

	public Parser(String fileName) {
		scanner = new Scanner(fileName);
	}

	public void parse() {
		computation();
	}

	/**
	 * computation = 'main' {varDecl} {funcDecl} '{' statSequence '}' '.' 
	 */
	public void computation() {
		getToken();
		expect(Kind.MAIN);

		while (currentToken.kind != Kind.EOF && currentToken.kind != Kind.ERROR && currentToken.kind != Kind.PERIOD){
			if(accept(Kind.VAR) || accept(Kind.ARRAY)) {  //first set of var decl
				varDeclaration();
			}
	
			else if(accept(Kind.FUNCTION) || accept(Kind.PROCEDURE)){
				//TODO implemention of function or procedure delcaration
			}
	
			if (accept(Kind.BEGIN)) {
				getToken();  //eat the open brace
				statSequence();
				expect(Kind.END);
				// got an open brace
	
			}
		} 
		expect(Kind.PERIOD);
	}
	
	/**
	 * typeDecl ident { ',' ident } ';'
	 */
	private void varDeclaration() {
		typeDecl();
		ident();
		while(accept(Kind.COMMA)) {
			getToken(); // eat the comma
			ident();
		} 
		expect(Kind.SEMI_COL);
	}

	/**
	 * typeDecl = 'var' | 'array' '[' number ']' { '[' number ']' }
	 */
	private void typeDecl() {
		if(currentToken.kind == Kind.VAR) {
			getToken(); //eat the var token
		}
		else if(currentToken.kind == Kind.ARRAY) {
			//array declaration
		}
	}

	/**
	 * statement { ';' statement }
	 */
	private void statSequence() {
		statement();
		while(accept(Kind.SEMI_COL)){
			getToken();  //eat the semicolon
			statement();
		}
		
	};

	/**
	 * statement = assignment | funcCall | ifStatement | whileStatement | returnStatement
	 */
	private void statement() {
		if(accept(Kind.LET)) {
			assignment();
		}
		else if (accept(Kind.CALL)) {
			funcCall();
		}
		else if(accept(Kind.IF)) {
			//ifStatement();
		}
		else if(accept(Kind.WHILE)) {
			//whileStatement();
		}
		else if(accept(Kind.RETURN)) {
			//returnStatement();
		}
	}

	/**
	 * assignment = 'let' designator '<-' expression
	 */
	public void assignment() {
		expect(Kind.LET);
		designator();
		expect(Kind.BECOMES);
		expression();
	}

	/**
	 * funcCall = 'call' ident [ '(' [expression { ',' expression } ] ')' ]
	 */
	public void funcCall() {
		expect(Kind.CALL);
		ident();
		expect(Kind.OPN_PAREN);
		expression();
		while(accept(Kind.COMMA)) {
			expression();
		}
		expect(Kind.CLS_PAREN);
	}

	/**
	 * designator = ident { '[' expression ']' }
	 */
	public void designator() {
		ident(); //TODO call identifier, dont just eat it???
		while(accept(Kind.OPN_BRACK)) {
			//expression();  //TODO implement expression to handle array declarations
			//expect(Kind.CLS_BRACK);
		}
	}

	/**
	 * expression = term  { ('+' | '-') term }
	 */
	public void expression() {
		term();
	}

	/**
	 * term = factor { ('*' | '/') factor }
	 */
	public void term() {
		factor();
		while(accept(Kind.TIMES) || accept(Kind.DIV)) {
			getToken();  //eat the times or div
			factor();
		}
	}

	/**
	 * factor = designator | number | '(' expression ')' | funcCall
	 */
	public void factor() {
		if(accept(Kind.IDENTIFIER)) {
			designator();
		}
		else if(accept(Kind.NUMBER)) {
			number();
		}
		else if(accept(Kind.OPN_PAREN)) {
			expression();
		}
		else if(accept(Kind.CALL)) {
			funcCall();
        }
		else {
			error();
		} 
	}

	/**
	 * ident = letter { letter | digit }
	 */
	public void ident() { //TODO need to determine how to return a node here
		System.out.println("Identifier found: " + currentToken.getLexeme());
		getToken();
	}

	/**
	 * number = digit {digit}
	 */
	public void number() {
		System.out.println("Number found: " + currentToken.getLexeme());
		getToken();
	}

	/*
	 * Determines if the current token matches the expected token.
	 * If it does, we eat the token, otherwise the token was not what we
	 * expected during parsing and throw and error.
	 * kind the expected token kind that should be accepted
	 */
	private void expect(Kind kind){
		if(currentToken.kind != kind) {
			error(kind);
		}
		getToken();
	}

	/*
	 * 
	 */
	private boolean accept(Kind kind) {
		if(currentToken.kind == kind){
			return true;
		}
		return false;
	}

	private void getToken(){
		currentToken = scanner.nextToken();
	}


//	private boolean tokenFirstSet(Kind currentKind, Kind...kinds) {
//		for(Kind kind : kinds){
//			if(currentKind == kind){
//				return true;
//			}
//		}
//		return false;
//	}

	private void error(Kind expected) {
		System.err.println("Syntax error.  Expected: " + expected.name() + " but recieved " + currentToken.kind.name()); 
		LOGGER.error("Syntax error.  Expected: " + expected.name() + " but recieved " + currentToken.kind.name());
		System.exit(1);
	}

	private void error() {
		System.err.println("Syntax error. Did not recieve an expected token.");
		LOGGER.error("Syntax error. Did not recieve an expected token.");
		System.exit(1);
	}
}
