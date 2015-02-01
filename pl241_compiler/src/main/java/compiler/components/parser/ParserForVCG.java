package compiler.components.parser;

import static compiler.components.parser.ParserUtils.combineArithmetic;
import static compiler.components.parser.ParserUtils.emitAssignmentInstruction;
import static compiler.components.parser.ParserUtils.combineRelation;
import static compiler.components.parser.ParserUtils.conditionalJumpForward;
import static compiler.components.parser.ParserUtils.createUnconditionBranch;
import static compiler.components.parser.ParserUtils.updateSymbols;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermeditate_rep.BasicBlock;
import compiler.components.intermeditate_rep.Instruction;
import compiler.components.intermeditate_rep.Instruction.InstructionBuilder;
import compiler.components.lex.Scanner;
import compiler.components.lex.Token;
import compiler.components.lex.Token.Kind;
import compiler.components.parser.Result.ResultEnum;
import compiler.components.parser.tree.Expression;
import compiler.components.parser.tree.FuncBody;
import compiler.components.parser.tree.FuncDecl;
import compiler.components.parser.tree.Ident;
import compiler.components.parser.tree.ReturnStatement;
import compiler.components.parser.tree.Statement;
import compiler.components.parser.tree.VarDecl;
import compiler.components.parser.tree.WhileStatement;

/**
 * Implementation of a top-down recursive descent parser.
 *
 * @author John Palomo, 60206611
 */
public class ParserForVCG {
	static Logger LOGGER = LoggerFactory.getLogger(ParserForVCG.class);

	Map<Integer, Instruction> programInstructions;
	public String fileName;
	public Token currentToken;
	public BasicBlock beginBlock;  //hold a reference to the beinning of the program
	private BasicBlock currentBB;
	private Scanner scanner;

	public ParserForVCG(String fileName) {
		this.fileName = fileName;
		scanner = new Scanner(fileName);

		
	}

	public ParserForVCG parse() throws ParsingException {
		getToken(); //get the first token
		computation();
		return this;
	}

	/**
	 * computation = 'main' {varDecl} {funcDecl} '{' statSequence '}' '.' 
	 * @throws ParsingException 
	 */
	private void computation() throws ParsingException {
		expect(Kind.MAIN);

		while (currentToken.kind != Kind.EOF && currentToken.kind != Kind.ERROR && currentToken.kind != Kind.PERIOD) {
			if(accept(Kind.VAR) || accept(Kind.ARRAY)) {  //first set of var decl
				varDecl();
			}
			else if(accept(Kind.FUNCTION) || accept(Kind.PROCEDURE)){
				funcDecl();  //TODO add to symbole table?
			}
			else if (accept(Kind.BEGIN)) {
				getToken();  //eat the open brace
				currentBB = new BasicBlock();
				beginBlock = currentBB;
				statSequence();
				expect(Kind.END);
			}
			else {
				throw new ParsingException();
			}
		} 
		expect(Kind.PERIOD);
		return;  
	}
	
	/**
	 * varDecl = typeDecl ident { ',' ident } ';'
	 * @throws ParsingException 
	 */
	private List<VarDecl> varDecl() throws ParsingException {
		List<VarDecl> varDecls = new ArrayList<VarDecl>();

		typeDecl();  //TODO dont need the type at this point for the parse tree

		Result ident = ident();
		ParserUtils.addSymbol(ident.varValue);

		while(accept(Kind.COMMA)) {
			getToken(); // eat the comma
			ident = ident();
			ParserUtils.addSymbol(ident.varValue);
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

		Ident funcName = null;

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
			//params.add(ident());
			while(accept(Kind.COMMA)) {
				getToken();  //eat the comma
			//	params.add(ident());
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
			statSequence();
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
	private Result statSequence() throws ParsingException {
		Result x = statement();
		while(accept(Kind.SEMI_COL)){
			getToken();  //eat the semicolon
			x = statement();
		}

		return x;
	}

	/**
	 * statement = assignment | funcCall | ifStatement | whileStatement | returnStatement
	 * @throws ParsingException 
	 */
	private Result statement() throws ParsingException {
		Result result = null;
		if(accept(Kind.LET)) {
			result = assignment();
		}
		else if (accept(Kind.CALL)) {
			funcCall();
		}
		else if(accept(Kind.IF)) {
			BasicBlock before = currentBB;
			result = ifStatement();
		}
		else if(accept(Kind.WHILE)) {
			WhileStatement whileStatement = whileStatement();
			//statement = Statement.builder(getLineNum()).setWhileStatement(whileStatement).build();
		}
		else if(accept(Kind.RETURN)) {
			ReturnStatement returnStatement = returnStatement();
			//statement = Statement.builder(getLineNum()).setReturnStatement(returnStatement).build();
		}
		else {
			throw new ParsingException();
		}
		
		return result;
	}

	/**
	 * assignment = 'let' designator '<-' expression
	 * @throws ParsingException 
	 */
	private Result assignment() throws ParsingException {
		expect(Kind.LET);

		Result x = designator();

		expect(Kind.BECOMES);

		Result y = expression();

		updateSymbols(x.varValue);  //once we write we update the instruction number, update symbols before writing
		x = emitAssignmentInstruction(x, y, currentBB);
		return x; 
	}

	/**
	 * funcCall = 'call' ident [ '(' [expression { ',' expression } ] ')' ]
	 * @throws ParsingException 
	 */
	private Result funcCall() throws ParsingException {
		expect(Kind.CALL);

		Result result = ident();
	
        InstructionBuilder iBuilder = Instruction.builder(ParserUtils.getNewInstCount());
        iBuilder.operator(result.varValue);
		if(accept(Kind.OPN_PAREN)) {
			getToken();  //eat the open paren
			Result expression = expression();

			while(accept(Kind.COMMA)) {
				getToken(); //eat the comma
				expression = expression();
			}
			iBuilder.op1(expression.varValue);
			expect(Kind.CLS_PAREN);
		}
        currentBB.addInstruction(iBuilder.buildFunctionCall()); 

		return result;
	}

	/**
	 * ifStatement = 'if' relation 'then' statSequence [ 'else' statSequence ] 'fi' 
	 * @throws ParsingException 
	 */
	private Result ifStatement() throws ParsingException {
		expect(Kind.IF);
		
		BasicBlock before = currentBB;
		BasicBlock condBB = createBBWithDomInfo(currentBB, true);  //create the condition Block
		addControlFlow(before, condBB);
		Result relation = relation();  //might have fixups

		expect(Kind.THEN);

		BasicBlock ifBodyBB = createBBWithDomInfo(condBB, true);
		addControlFlow(condBB, ifBodyBB);

		Result ifBody = statSequence();  //returns the first result

		BasicBlock joinBlock = null; 
		BasicBlock elseBodyBB = null;
		if(accept(Kind.ELSE)) {
			joinBlock = createBBWithDomInfo(condBB, false); //create join block pointer, only create if you have an else
			addControlFlow(ifBodyBB, joinBlock);
			getToken(); //eat the else
			elseBodyBB = createBBWithDomInfo(condBB, true);
			addControlFlow(condBB, elseBodyBB);
			addControlFlow(elseBodyBB, joinBlock);
			Result elseBody = statSequence();
		}

		if(elseBodyBB != null) {
			conditionalJumpForward(condBB, elseBodyBB.blockNumber.toString()); 
			createUnconditionBranch(ifBodyBB, joinBlock.blockNumber.toString());  //jump to following block
			currentBB = joinBlock;
		}
		else {
			//dont need to branch from if body, just fall through, condition will branch here
			conditionalJumpForward(condBB, String.valueOf(ParserUtils.blockCount + 1)); 
		}

		expect(Kind.FI);

		
		return relation;
	}



	/**
	 * whileStatement = 'while' relation 'do' statSequence 'od'
	 * @throws ParsingException 
	 */
	private WhileStatement whileStatement() throws ParsingException {
		currentBB = ParserUtils.createBBWithDomRelationship(currentBB);
		expect(Kind.WHILE);
		Result relation = relation();
		expect(Kind.DO);
	//	List<Statement> statSequence = statSequence();
		expect(Kind.OD);

		return null;
	}

	/**
	 * returnStatement = 'return' [ expression ]
	 * @throws ParsingException 
	 */
	private ReturnStatement returnStatement() throws ParsingException {
		expect(Kind.RETURN);

		Expression expression = null;
		if(accept(Kind.IDENTIFIER) || accept(Kind.NUMBER)) {
			//expression = expression();
		}
		else if(accept(Kind.OPN_PAREN) || accept(Kind.FUNCTION) || accept(Kind.PROCEDURE)) { //identifier is not)
			getToken();  //eat the paren, 'function', or 'procedure' token
			//expression = expression();
		}

		ReturnStatement returnStatement = new ReturnStatement(getLineNum(), null);
		return returnStatement;
	}
	
	/**
	 * designator = ident { '[' expression ']' }
	 * @throws ParsingException 
	 */
	private Result designator() throws ParsingException {
		Result x = ident();
		List<Expression> arrayExpr = new ArrayList<Expression>();
		while(accept(Kind.OPN_BRACK)) {
			getToken(); //eat the open bracket
			//arrayExpr.add(expression()); 
			expect(Kind.CLS_BRACK);
		}


		//Designator designator = new Designator(getLineNum(), ident, arrayExpr);
		return x;
	}

	/**
	 * expression = term  { ('+' | '-') term }
	 * @throws ParsingException 
	 */
	private Result expression() throws ParsingException {
		Result result1 = term();
		if(accept(Kind.PLUS) || accept(Kind.MINUS)) {
			do {
				String op = currentToken.getLexeme(); 
				getToken();  //eat the operator
	
				Result result2 = term();
	
				result1 = combineArithmetic(result1, op, result2, currentBB); 
			}while(accept(Kind.PLUS) || accept(Kind.MINUS)); 
		}
		return result1;
	}

	/**
	 * term = factor { ('*' | '/') factor }
	 * @throws ParsingException 
	 */
	private Result term() throws ParsingException {
		Result result1 = factor();
		if(accept(Kind.TIMES) || accept(Kind.DIV)) {
			do {
				String op = currentToken.getLexeme(); 

				getToken();  //eat the times or div;
	
				Result result2 = factor();

				result1 = combineArithmetic(result1, op, result2, currentBB);
				
			}while(accept(Kind.TIMES) || accept(Kind.DIV));
		}
		return result1;
	}

	/**
	 * relation = expression relOp expression
	 * @throws ParsingException 
	 */
	private Result relation() throws ParsingException {
		Result leftExpr = expression();

		String op = currentToken.getLexeme();
		getToken();
		Result rightExpr = expression();
		
		Result x = combineRelation(leftExpr, op, rightExpr, currentBB);
		
		return x;
	}

	/**
	 * factor = designator | number | '(' expression ')' | funcCall
	 * @throws ParsingException 
	 */
	private Result factor() throws ParsingException {

		Result x = new Result();

		if(accept(Kind.IDENTIFIER)) {
			x = designator();
			x.varValue = ParserUtils.getSymbolFromTable(x.varValue);
		}
		else if(accept(Kind.NUMBER)) {
			x = number();
		}

//		else if(accept(Kind.OPN_PAREN)) {
//			Expression expression = expression();
//
//			if(expression.isNumber()) {
//				Number number = new Number(getLineNum(), expression.getNumberValue());
//				factor = Factor.builder(getLineNum()).setNumber(number).build();
//			}
//			else {
//				factor = Factor.builder(getLineNum()).setExpression(expression).build();
//			}
//		}
//		else if(accept(Kind.CALL)) {
//			FuncCall funcCall = funcCall();
//			factor = Factor.builder(getLineNum()).setFuncCall(funcCall).build();
//        }
		else {
			throw new ParsingException();
		} 
		
		return x;
	}

	/**
	 * ident = letter { letter | digit }
	 */
	private Result ident() {
		System.out.println("Identifier found: " + currentToken.getLexeme());
		Result x = new Result();
		x.type = ResultEnum.VARIABLE;
		x.varValue = currentToken.getLexeme();
		getToken();
		return x;
	}

	/**
	 * number = digit {digit}
	 */
	private Result number() {
		System.out.println("Number found: " + currentToken.getLexeme());

		Result x = new Result();
		x.type = ResultEnum.CONSTANT;
		x.constValue = Integer.valueOf(currentToken.getLexeme());

		getToken(); //eat the number
		return x;
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

	public int getLineNum() {
		return scanner.lineNum;
	}

	private BasicBlock createBBWithDomInfo(BasicBlock dominator, boolean setToCurrent){
		BasicBlock bb = new BasicBlock();
		addDomInfo(dominator, bb);
		if(setToCurrent){
			currentBB = bb;
		}
		return bb; 
	}

	public void addControlFlow(BasicBlock from, BasicBlock to){
		from.addControlFlow(to);
	}

	public void addDomInfo(BasicBlock dominator, BasicBlock dominatee){
		dominator.addDominatee(dominatee);
		dominatee.addDominator(dominator);
	}
}