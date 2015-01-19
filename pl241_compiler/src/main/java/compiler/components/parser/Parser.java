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
		getToken(); //get the first token
		computation();
	}

	/*
	 * computation = 'main' {varDecl} {funcDecl} '{' statSequence '}' '.' 
	 */
	private void computation() {
		expect(Kind.MAIN);

		while (currentToken.kind != Kind.EOF && currentToken.kind != Kind.ERROR && currentToken.kind != Kind.PERIOD){
			if(accept(Kind.VAR) || accept(Kind.ARRAY)) {  //first set of var decl
				varDecl();
			}
	
			else if(accept(Kind.FUNCTION) || accept(Kind.PROCEDURE)){
				funcDecl();
			}
	
			else if (accept(Kind.BEGIN)) {
				getToken();  //eat the open brace
				statSequence();
				expect(Kind.END);
			}
			else error();
		} 
		expect(Kind.PERIOD);
	}
	
	/*
	 * varDecl = typeDecl ident { ',' ident } ';'
	 */
	private void varDecl() {
		typeDecl();
		ident();
		while(accept(Kind.COMMA)) {
			getToken(); // eat the comma
			ident();
		} 
		expect(Kind.SEMI_COL);
	}

	/*
	 * funcDecl = ('function' | 'procedure') ident [formalParam] ';' funcBody ';'
	 */
	private void funcDecl() {
		getToken();  //eat function or procedure token
		ident();

		if(accept(Kind.OPN_PAREN)) {
			formalParam();
		} 
		expect(Kind.SEMI_COL);

		funcBody();
		expect(Kind.SEMI_COL);
	}

	/*
	 * formalParam = '(' [ident { ',' ident }] ')'
	 */
	private void formalParam() {
		expect(Kind.OPN_PAREN);
		
		if(accept(Kind.IDENTIFIER)) {
			ident();
			while(accept(Kind.COMMA)) {
				getToken();  //eat the comma
				ident();
			}
		} 

		expect(Kind.CLS_PAREN); 
	}

	/*
	 * funcBody = { varDecl } '{' [statSequence] '}' 
	 */
	private void funcBody() {
		while(accept(Kind.VAR) || accept(Kind.ARRAY)) {
			varDecl();
		}

		expect(Kind.BEGIN);

		if(FirstSets.STATEMENT.contains(currentToken.getLexeme())) {
			statSequence();
		}

		expect(Kind.END); 
	}

	/*
	 * typeDecl = 'var' | 'array' '[' number ']' { '[' number ']' }
	 */
	private void typeDecl() {
		if(currentToken.kind == Kind.VAR) {
			getToken(); //eat the var token
		}
		else if(currentToken.kind == Kind.ARRAY) {
			getToken(); // eat the array token
			expect(Kind.OPN_BRACK);
			number();
			expect(Kind.CLS_BRACK);

			while(accept(Kind.OPN_BRACK)) {
				getToken(); //eat the open bracket
				number();
				expect(Kind.CLS_BRACK);
			}
		}
	}

	/*
	 * statement { ';' statement }
	 */
	private void statSequence() {
		statement();
		while(accept(Kind.SEMI_COL)){
			getToken();  //eat the semicolon
			statement();
		}
		
	};

	/*
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
			ifStatement();
		}
		else if(accept(Kind.WHILE)) {
			whileStatement();
		}
		else if(accept(Kind.RETURN)) {
			returnStatement();
		}
	}

	/*
	 * assignment = 'let' designator '<-' expression
	 */
	private void assignment() {
		expect(Kind.LET);
		designator();
		expect(Kind.BECOMES);
		expression();
	}

	/*
	 * funcCall = 'call' ident [ '(' [expression { ',' expression } ] ')' ]
	 */
	private void funcCall() {
		expect(Kind.CALL);
		ident();
		if(accept(Kind.OPN_PAREN)) {
			getToken();  //eat the open paren
			expression();
			while(accept(Kind.COMMA)) {
				getToken(); //eat the comma
				expression();
			}
			expect(Kind.CLS_PAREN);
		}
	}

	/*
	 * ifStatement = 'if' relation 'then' statSequence [ 'else' statSequence ] 'fi' 
	 */
	private void ifStatement() {
		expect(Kind.IF);
		relation();
		expect(Kind.THEN);
		statSequence();

		if(accept(Kind.ELSE)) {
			getToken(); //eat the else
			statSequence();
		}
		
		expect(Kind.FI);
	}

	/*
	 * whileStatement = 'while' relation 'do' statSequence 'od'
	 */
	private void whileStatement() {
		expect(Kind.WHILE);
		relation();
		expect(Kind.DO);
		statSequence();
		expect(Kind.OD);
	}

	/*
	 * returnStateement = 'return' [ expression ]
	 */
	private void returnStatement() {
		expect(Kind.RETURN);

		if(accept(Kind.IDENTIFIER)) {
			expression();
		}
	}
	
	/*
	 * designator = ident { '[' expression ']' }
	 */
	private void designator() {
		ident(); //TODO call identifier, dont just eat it???
		while(accept(Kind.OPN_BRACK)) {
			getToken(); //eat the open bracket
			expression(); 
			expect(Kind.CLS_BRACK);
		}
	}

	/*
	 * expression = term  { ('+' | '-') term }
	 */
	private void expression() {
		term();
		while(accept(Kind.PLUS) || accept(Kind.MINUS)) {
			getToken();
			term();
		}
	}

	/*
	 * term = factor { ('*' | '/') factor }
	 */
	private void term() {
		factor();
		while(accept(Kind.TIMES) || accept(Kind.DIV)) {
			getToken();  //eat the times or div
			factor();
		}
	}

	/*
	 * relation = expression relOp expression
	 */
	private void relation() {
		expression();
		getToken();//TODO:  do relational comparison here
		expression();
	}

	/*
	 * factor = designator | number | '(' expression ')' | funcCall
	 */
	private void factor() {
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

	/*
	 * ident = letter { letter | digit }
	 */
	private void ident() { //TODO need to determine how to return a node here
		System.out.println("Identifier found: " + currentToken.getLexeme());
		getToken();
	}

	/*
	 * number = digit {digit}
	 */
	private void number() {
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
	 * Determines whether the current token's kind matches the 
	 * formal parameter kind 
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
