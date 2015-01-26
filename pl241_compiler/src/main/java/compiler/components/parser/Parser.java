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
import compiler.components.parser.tree.Factor;
import compiler.components.parser.tree.FuncBody;
import compiler.components.parser.tree.FuncCall;
import compiler.components.parser.tree.FuncDecl;
import compiler.components.parser.tree.Ident;
import compiler.components.parser.tree.IfStatement;
import compiler.components.parser.tree.Number;
import compiler.components.parser.tree.Relation;
import compiler.components.parser.tree.ReturnStatement;
import compiler.components.parser.tree.Statement;
import compiler.components.parser.tree.Symbol;
import compiler.components.parser.tree.Term;
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
		List<Statement> statSequence = null;
		
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
				statSequence = statSequence();
				expect(Kind.END);
			}
			else {
				throw new ParsingException();
			}
		} 
		expect(Kind.PERIOD);
		Computation computation = new Computation(getLineNum(), varDeclList, funcDeclList, statSequence);
		return computation;  
	}
	
	/**
	 * varDecl = typeDecl ident { ',' ident } ';'
	 * @throws ParsingException 
	 */
	private List<VarDecl> varDecl() throws ParsingException {
		List<VarDecl> varDecls = new ArrayList<VarDecl>();

		typeDecl();  //TODO dont need the type at this point for the parse tree

		Ident ident = ident();
		VarDecl varDecl = new VarDecl(getLineNum(), ident);

		varDecls.add(varDecl); 
		while(accept(Kind.COMMA)) {
			getToken(); // eat the comma
			ident = ident();
			varDecls.add(new VarDecl(getLineNum(), ident));
		} 
		expect(Kind.SEMI_COL);
		
		return varDecls;
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

		FuncDecl funcDecl = new FuncDecl(getLineNum(), funcName, formalParams, funcBody);
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
		
		FuncBody funcBody = new FuncBody(getLineNum(), varDecls, statements);
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
			statements.add(statement());
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
			statement = Statement.builder(getLineNum()).setAssignment(assignment).build();
		}
		else if (accept(Kind.CALL)) {
			funcCall();
		}
		else if(accept(Kind.IF)) {
			IfStatement ifStatement = ifStatement();
			statement = Statement.builder(getLineNum()).setIfStatement(ifStatement).build();
		}
		else if(accept(Kind.WHILE)) {
			WhileStatement whileStatement = whileStatement();
			statement = Statement.builder(getLineNum()).setWhileStatement(whileStatement).build();
		}
		else if(accept(Kind.RETURN)) {
			ReturnStatement returnStatement = returnStatement();
			statement = Statement.builder(getLineNum()).setReturnStatement(returnStatement).build();
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

		Assignment assignment = new Assignment(getLineNum(), designator, expression);
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

		FuncCall funcCall = new FuncCall(getLineNum(), funcIdent, expressions);
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

		ifStat = new IfStatement(getLineNum(), relation, ifBody, elseBody);
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

		WhileStatement whileStatement = new WhileStatement(getLineNum(), relation, statSequence);
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

		ReturnStatement returnStatement = new ReturnStatement(getLineNum(), expression);
		return returnStatement;
	}
	
	/**
	 * designator = ident { '[' expression ']' }
	 * @throws ParsingException 
	 */
	private Designator designator() throws ParsingException {
		Ident ident = ident();
		List<Expression> arrayExpr = new ArrayList<Expression>();
		while(accept(Kind.OPN_BRACK)) {
			getToken(); //eat the open bracket
			arrayExpr.add(expression()); 
			expect(Kind.CLS_BRACK);
		}

		Designator designator = new Designator(getLineNum(), ident, arrayExpr);
		return designator;
	}

	/**
	 * expression = term  { ('+' | '-') term }
	 * @throws ParsingException 
	 */
	private Expression expression() throws ParsingException {
		Expression expression = null;
		Term term1 = term();
		if(accept(Kind.PLUS) || accept(Kind.MINUS)) {
			do {
				Symbol op = new Symbol(currentToken.getLexeme()); 
				getToken();  //eat the operator
	
				Term term2 = term();
	
                expression = Expression.builder(getLineNum()).setTerm1(term1).setOp(op).setTerm2(term2).build();
			}while(accept(Kind.PLUS) || accept(Kind.MINUS)); 
		}
		else {
			expression = Expression.builder(getLineNum()).setTerm1(term1).build();
		}
		return expression;
	}

	/**
	 * term = factor { ('*' | '/') factor }
	 * @throws ParsingException 
	 */
	private Term term() throws ParsingException {
		Factor factor1 = factor();
		Term term = new Term(getLineNum(), factor1, null, null);
		if(accept(Kind.TIMES) || accept(Kind.DIV)) {
			do {
				Symbol op = new Symbol(currentToken.getLexeme());  //
				getToken();  //eat the times or div;
	
				Factor factor2 = factor();
				term = new Term(getLineNum(), term.getFactor1(), op, factor2);
				
			}while(accept(Kind.TIMES) || accept(Kind.DIV));
		}
		return term;
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

		Relation relation = new Relation(getLineNum(), relOp, leftExpr, rightExpr);

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
			factor = Factor.builder(getLineNum()).setDesignator(designator).build();
		}
		else if(accept(Kind.NUMBER)) {
			Number number = number();
			factor = Factor.builder(getLineNum()).setNumber(number).build();
		}
		else if(accept(Kind.OPN_PAREN)) {
			Expression expression = expression();

			if(expression.isNumber()) {
				Number number = new Number(getLineNum(), expression.getNumberValue());
				factor = Factor.builder(getLineNum()).setNumber(number).build();
			}
			else {
				factor = Factor.builder(getLineNum()).setExpression(expression).build();
			}
		}
		else if(accept(Kind.CALL)) {
			FuncCall funcCall = funcCall();
			factor = Factor.builder(getLineNum()).setFuncCall(funcCall).build();
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
		Ident ident = new Ident(getLineNum(), symbol);
		getToken();
		return ident;
	}

	/**
	 * number = digit {digit}
	 */
	private Number number() {
		System.out.println("Number found: " + currentToken.getLexeme());
		Number number = new Number(getLineNum(), Integer.valueOf(currentToken.getLexeme()));
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

	public int getLineNum() {
		return scanner.lineNum;
	}
}