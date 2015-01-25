package compiler.components.parser;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.lex.Scanner;
import compiler.components.lex.Token;
import compiler.components.lex.Token.Kind;
import compiler.components.parser.tree.Assignment;
import compiler.components.parser.tree.Computation;
import compiler.components.parser.tree.Designator;
import compiler.components.parser.tree.Expression;
import compiler.components.parser.tree.Expression.ExpressionType;
import compiler.components.parser.tree.Factor;
import compiler.components.parser.tree.Factor.FactorType;
import compiler.components.parser.tree.ReturnStatement;
import compiler.components.parser.tree.Statement.StatementBuilder;
import compiler.components.parser.tree.FuncBody;
import compiler.components.parser.tree.FuncCall;
import compiler.components.parser.tree.FuncDecl;
import compiler.components.parser.tree.Ident;
import compiler.components.parser.tree.IfStatement;
import compiler.components.parser.tree.Number;
import compiler.components.parser.tree.Relation;
import compiler.components.parser.tree.Statement;
import compiler.components.parser.tree.Symbol;
import compiler.components.parser.tree.VarDecl;
import compiler.components.parser.tree.WhileStatement;

/**
 * Implementation of a top-down recursive descent parser.
 *
 * @author John Palomo, 60206611
 */
public class Parser {

	private Scanner scanner;
	public Token currentToken;
	private Computation computationNode;

	static Logger LOGGER = LoggerFactory.getLogger(Parser.class);

	public Parser(String fileName) {
		scanner = new Scanner(fileName);
	}

	public Parser parse() throws ParsingException {
		getToken(); //get the first token
		computationNode = computation();
		return this;
	}

	/**
	 * computation = 'main' {varDecl} {funcDecl} '{' statSequence '}' '.' 
	 * @throws ParsingException 
	 */
	private Computation computation() throws ParsingException {
		expect(Kind.MAIN);

		List<VarDecl> varDeclList = new ArrayList<VarDecl>();
		List<FuncDecl> funcDeclList = new ArrayList<FuncDecl>();	
		List<Statement> statSequence = new ArrayList<Statement>();
		
		while (currentToken.kind != Kind.EOF && currentToken.kind != Kind.ERROR && currentToken.kind != Kind.PERIOD) {
			if(accept(Kind.VAR) || accept(Kind.ARRAY)) {  //first set of var decl
				List<VarDecl> varSymbols = varDecl();
				varDeclList.addAll(varSymbols);
			}
	
			else if(accept(Kind.FUNCTION) || accept(Kind.PROCEDURE)){
				FuncDecl funcDecl = funcDecl();
				funcDeclList.add(funcDecl);
			}
	
			else if (accept(Kind.BEGIN)) {
				getToken();  //eat the open brace
				List<Statement> statements = statSequence();
				statSequence.addAll(statements); //TODO might be able to remove this and just add return the list to statsequece list obj
				expect(Kind.END);
			}
			else {
				throw new ParsingException();
			}
		} 
		expect(Kind.PERIOD);
		Computation computation = new Computation(scanner.lineNum, scanner.charPos, varDeclList, funcDeclList, statSequence);
		return computation;  
	}
	
	/**
	 * varDecl = typeDecl ident { ',' ident } ';'
	 * @throws ParsingException 
	 */
	private List<VarDecl> varDecl() throws ParsingException {
		List<VarDecl> symbols = new ArrayList<VarDecl>();

		typeDecl();  //TODO dont need the type at this point for the parse tree

		Ident ident = ident();
		symbols.add(new VarDecl(scanner.lineNum, scanner.charPos, ident));

		while(accept(Kind.COMMA)) {
			getToken(); // eat the comma
			ident = ident();
			symbols.add(new VarDecl(scanner.lineNum, scanner.charPos, ident));
		} 
		expect(Kind.SEMI_COL);
		
		return symbols;
	}

	/**
	 * funcDecl = ('function' | 'procedure') ident [formalParam] ';' funcBody ';'
	 * @throws ParsingException 
	 */
	private FuncDecl funcDecl() throws ParsingException {
		getToken();  //eat function or procedure token

		Ident funcName = ident();

		List<Ident> formalParams = null;
		if(accept(Kind.OPN_PAREN)) {
			formalParams = formalParam();
		} 
		expect(Kind.SEMI_COL);

		FuncBody funcBody = funcBody();

		expect(Kind.SEMI_COL);

		FuncDecl funcDecl = new FuncDecl(scanner.lineNum, scanner.charPos, funcName, formalParams, funcBody);
		return funcDecl;
	}

	/**
	 * formalParam = '(' [ident { ',' ident }] ')'
	 * @throws ParsingException 
	 */
	private List<Ident> formalParam() throws ParsingException {
		expect(Kind.OPN_PAREN);
		
		List<Ident> params = new ArrayList<Ident>();
		if(accept(Kind.IDENTIFIER)) {
			params.add(ident());
			while(accept(Kind.COMMA)) {
				getToken();  //eat the comma
				params.add(ident());
			}
		} 

		expect(Kind.CLS_PAREN); 
		return params;
	}

	/**
	 * funcBody = { varDecl } '{' [statSequence] '}' 
	 * @throws ParsingException 
	 */
	private FuncBody funcBody() throws ParsingException {
		List<VarDecl> varDecls = null;
		while(accept(Kind.VAR) || accept(Kind.ARRAY)) {
			varDecls = varDecl();
		}

		expect(Kind.BEGIN);

		List<Statement> statements = null;
		if(FirstSets.STATEMENT.contains(currentToken.getLexeme())) {
			statements = statSequence();
		}

		expect(Kind.END); 
		
		FuncBody funcBody = new FuncBody(scanner.lineNum, scanner.charPos, varDecls, statements);
		return funcBody;
		 
	}

	/**
	 * typeDecl = 'var' | 'array' '[' number ']' { '[' number ']' }
	 * @throws ParsingException 
	 */
	private void typeDecl() throws ParsingException {
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

	/**
	 * statSequence = statement { ';' statement }
	 * @throws ParsingException 
	 */
	private List<Statement> statSequence() throws ParsingException {
		List<Statement> statements = new ArrayList<Statement>();
		statements.add(statement());
		while(accept(Kind.SEMI_COL)){
			getToken();  //eat the semicolon
			statement();
		}
		return statements;
	}

	/**
	 * statement = assignment | funcCall | ifStatement | whileStatement | returnStatement
	 * @throws ParsingException 
	 */
	private Statement statement() throws ParsingException {
		Statement statement = null;
		if(accept(Kind.LET)) {
			Assignment assignment = assignment();
			statement = Statement.builder(assignment.getLineNum(), assignment.getCharPos()).setAssignment(assignment).build();
		}
		else if (accept(Kind.CALL)) {
			funcCall();
		}
		else if(accept(Kind.IF)) {
			IfStatement ifStatement = ifStatement();
			statement = Statement.builder(ifStatement.getLineNum(), ifStatement.getCharPos()).setIfStatement(ifStatement).build();
		}
		else if(accept(Kind.WHILE)) {
			WhileStatement whileStatement = whileStatement();
			statement = Statement.builder(whileStatement.getLineNum(), whileStatement.getCharPos()).setWhileStatement(whileStatement).build();
		}
		else if(accept(Kind.RETURN)) {
			ReturnStatement returnStatement = returnStatement();
			statement = Statement.builder(returnStatement.getLineNum(), returnStatement.getCharPos()).setReturnStatement(returnStatement).build();
		}
		return statement;
	}

	/**
	 * assignment = 'let' designator '<-' expression
	 * @throws ParsingException 
	 */
	private Assignment assignment() throws ParsingException {
		expect(Kind.LET);

		Designator designator = designator();
		
		expect(Kind.BECOMES);

		Expression expression = expression();

		Assignment assignment = new Assignment(scanner.lineNum, scanner.charPos, designator, expression);
		return assignment; 
	}

	/**
	 * funcCall = 'call' ident [ '(' [expression { ',' expression } ] ')' ]
	 * @throws ParsingException 
	 */
	private FuncCall funcCall() throws ParsingException {
		expect(Kind.CALL);
		Ident funcIdent = ident();
		List<Expression> expressions = new ArrayList<Expression>();
		
		if(accept(Kind.OPN_PAREN)) {
			getToken();  //eat the open paren
			Expression expression = expression();
			expressions.add(expression);

			while(accept(Kind.COMMA)) {
				getToken(); //eat the comma
				expression = expression();
				expressions.add(expression);
			}
			expect(Kind.CLS_PAREN);
		}

		FuncCall funcCall = new FuncCall(scanner.lineNum, scanner.charPos, funcIdent, expressions);
		return funcCall;
	}

	/**
	 * ifStatement = 'if' relation 'then' statSequence [ 'else' statSequence ] 'fi' 
	 * @throws ParsingException 
	 */
	private IfStatement ifStatement() throws ParsingException {
		IfStatement ifStat;

		expect(Kind.IF);
		Relation relation = relation();
		expect(Kind.THEN);

		List<Statement> ifBody = statSequence();

		List<Statement> elseBody = null;
		if(accept(Kind.ELSE)) {
			getToken(); //eat the else
			elseBody = statSequence();
		}
		
		expect(Kind.FI);

		ifStat = new IfStatement(scanner.lineNum, scanner.charPos, relation, ifBody, elseBody);
		return ifStat;
	}

	/**
	 * whileStatement = 'while' relation 'do' statSequence 'od'
	 * @throws ParsingException 
	 */
	private WhileStatement whileStatement() throws ParsingException {
		expect(Kind.WHILE);
		Relation relation = relation();
		expect(Kind.DO);
		List<Statement> statSequence = statSequence();
		expect(Kind.OD);

		WhileStatement whileStatement = new WhileStatement(scanner.lineNum, scanner.charPos, relation, statSequence);
		return whileStatement;
	}

	/**
	 * returnStatement = 'return' [ expression ]
	 * @throws ParsingException 
	 */
	private ReturnStatement returnStatement() throws ParsingException {
		expect(Kind.RETURN);

		Expression expression = null;
		if(accept(Kind.IDENTIFIER) || accept(Kind.NUMBER)) {
			expression = expression();
		}
		else if(accept(Kind.OPN_PAREN) || accept(Kind.FUNCTION) || accept(Kind.PROCEDURE)) { //identifier is not)
			getToken();  //eat the paren, 'function', or 'procedure' token
			expression = expression();
		}

		ReturnStatement returnStatement = new ReturnStatement(scanner.lineNum, scanner.charPos, expression);
		return returnStatement;
	}
	
	/**
	 * designator = ident { '[' expression ']' }
	 * @throws ParsingException 
	 */
	private Designator designator() throws ParsingException {
		Ident ident = ident(); //TODO call identifier, dont just eat it???
		List<Expression> arrayExpr = new ArrayList<Expression>();
		while(accept(Kind.OPN_BRACK)) {
			getToken(); //eat the open bracket
			arrayExpr.add(expression()); 
			expect(Kind.CLS_BRACK);
		}

		Designator designator = new Designator(scanner.lineNum, scanner.charPos, ident, arrayExpr);
		return designator;
	}

	/**
	 * expression = term  { ('+' | '-') term }
	 * @throws ParsingException 
	 */
	private Expression expression() throws ParsingException {
		Expression expression = null;
		Factor factor1 = term();
		while(accept(Kind.PLUS) || accept(Kind.MINUS)) {
			Symbol operation = new Symbol(currentToken.getLexeme()); 
			getToken();

			Factor factor2 = term();

			if(factor1.getType().equals(FactorType.NUMBER) && factor1.getType().equals(FactorType.NUMBER)) {
				factor1 = Factor.combineFactors(factor1, factor2, operation);
				expression = new Expression(scanner.lineNum, scanner.charPos, ExpressionType.NUMBER);
			}
			else if(factor1.getType().equals(FactorType.IDENT) || factor2.getType().equals(FactorType.IDENT)) {
				expression = new Expression(scanner.lineNum, scanner.charPos, ExpressionType.EXPRESSION);
				expression.setExpression(factor1, factor2, operation);
			}
		}
		return expression;
	}

	/**
	 * term = factor { ('*' | '/') factor }
	 * @throws ParsingException 
	 */
	private Factor term() throws ParsingException {
		Factor factor1= factor();
		while(accept(Kind.TIMES) || accept(Kind.DIV)) {
			Symbol symbol = new Symbol(currentToken.getLexeme());
			getToken();  //eat the times or div;

			Factor factor2 = factor();

			if(factor1.getType().equals(FactorType.NUMBER) && factor2.getType().equals(FactorType.NUMBER)) {
				factor1 = Factor.combineFactors(factor1, factor2, symbol);
			}
		}
		return factor1;
	}

	/**
	 * relation = expression relOp expression
	 * @throws ParsingException 
	 */
	private Relation relation() throws ParsingException {
		Expression leftExpr = expression();

		Symbol relOp = new Symbol(currentToken.getLexeme());

		getToken();//TODO:  do relational comparison here??

		Expression rightExpr = expression();

		Relation relation = new Relation(scanner.lineNum, scanner.charPos, relOp, leftExpr, rightExpr);

		return relation;
	}

	/**
	 * factor = designator | number | '(' expression ')' | funcCall
	 * @throws ParsingException 
	 */
	private Factor factor() throws ParsingException {
		Factor factor; 

		if(accept(Kind.IDENTIFIER)) {
			Designator designator = designator();
			factor = Factor.builder(scanner.lineNum, scanner.charPos).setDesignator(designator).build();
		}
		else if(accept(Kind.NUMBER)) {
			Number number = number();
			factor = Factor.builder(scanner.lineNum, scanner.charPos).setNumber(number).build();
		}
		else if(accept(Kind.OPN_PAREN)) {
			Expression expression = expression();

			if(expression.getType().equals(ExpressionType.NUMBER)) {
				Number number = new Number(scanner.lineNum, scanner.charPos, expression.getValue());
				factor = Factor.builder(scanner.lineNum, scanner.charPos).setNumber(number).build();
			}
			else {
				factor = Factor.builder(scanner.lineNum, scanner.charPos).setExpression(expression).build();
			}
		}
		else if(accept(Kind.CALL)) {
			FuncCall funcCall = funcCall();
			factor = Factor.builder(scanner.lineNum, scanner.charPos).setFuncCall(funcCall).build();
        }
		else {
			throw new ParsingException();
		} 
		
		return factor;
	}

	/**
	 * ident = letter { letter | digit }
	 */
	private Ident ident() {
		System.out.println("Identifier found: " + currentToken.getLexeme());
		Symbol symbol = new Symbol(currentToken.getLexeme());
		Ident ident = new Ident(scanner.lineNum, scanner.charPos, symbol);
		getToken();
		return ident;
	}

	/**
	 * number = digit {digit}
	 */
	private Number number() {
		System.out.println("Number found: " + currentToken.getLexeme());
		Number number = new Number(scanner.lineNum, scanner.charPos, Integer.valueOf(currentToken.getLexeme()));
		getToken();
		return number;
	}

	/**
	 * Determines if the current token matches the expected token.
	 * If it does, we eat the token, otherwise the token was not what we
	 * expected during parsing and throw and error.
	 * kind the expected token kind that should be accepted
	 * @throws ParsingException 
	 */
	private void expect(Kind kind) throws ParsingException{
		if(currentToken.kind != kind) {
			throw new ParsingException(kind, currentToken);
		}
		getToken();
	}

	/**
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

	public Computation getComputationNode() {
		return computationNode;
	} 
}