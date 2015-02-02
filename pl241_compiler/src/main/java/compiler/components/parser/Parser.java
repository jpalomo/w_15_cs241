package compiler.components.parser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermeditate_rep.BasicBlock;
import compiler.components.intermeditate_rep.Result;
import compiler.components.intermeditate_rep.Result.ResultEnum;
import compiler.components.lex.Scanner;
import compiler.components.lex.Token;
import compiler.components.lex.Token.Kind;
import compiler.components.parser.tree.FuncBody;
import compiler.components.parser.tree.FuncDecl;
import compiler.components.parser.tree.Ident;
import compiler.components.parser.tree.ReturnStatement;

/**
 * Implementation of a top-down recursive descent parser.
 *
 * @author John Palomo, 60206611
 */
public class Parser {
	static Logger LOGGER = LoggerFactory.getLogger(Parser.class);

	public Token currentToken;
	public ParserUtils pUtils;
	public BasicBlock beginBlock; // hold a reference to the beginning of the
									// program
	private BasicBlock currentBB;
	private Scanner scanner;

	public Parser(String fileName) {
		scanner = new Scanner(fileName);
		pUtils = new ParserUtils();
	}

	public Parser parse() throws ParsingException {
		eatToken(); // get the first token
		computation();

		if(beginBlock.isEmpty()){
			//go to next block of control
			beginBlock = beginBlock.getControlFlow().get(0);
		}
		
		return this;
	}

	/** computation = 'main' {varDecl} {funcDecl} '{' statSequence '}' '.' */
	private void computation() throws ParsingException {
		expect(Kind.MAIN);

		while (currentToken.kind != Kind.EOF && currentToken.kind != Kind.ERROR && currentToken.kind != Kind.PERIOD) {
			// first set of var decl
			if (accept(Kind.VAR) || accept(Kind.ARRAY)) { 
				varDecl();
			} 
			else if (accept(Kind.FUNCTION) || accept(Kind.PROCEDURE)) {
				funcDecl(); // TODO add to symbole table?
			}
			else if (accept(Kind.BEGIN)) {
				eatToken(); // eat the open brace
				currentBB = new BasicBlock();
				beginBlock = currentBB;
				statSequence();
				expect(Kind.END);
			} else {
				throw new ParsingException();
			}
		}
		expect(Kind.PERIOD);
		return;
	}

	/** varDecl = typeDecl ident { ',' ident } ';' */
	private void varDecl() throws ParsingException {

		typeDecl(); // TODO dont need the type at this point for the parse tree

		Result ident = ident();
		pUtils.addSymbol(ident.varValue);

		while (accept(Kind.COMMA)) {
			eatToken(); // eat the comma
			ident = ident();
			pUtils.addSymbol(ident.varValue);
		}
		expect(Kind.SEMI_COL);
	}

	/** funcDecl = ('function' | 'procedure') ident [formalParam] ';' funcBody ';' */
	private FuncDecl funcDecl() throws ParsingException {
		eatToken(); // eat function or procedure token

		Ident funcName = null;

		List<Ident> formalParams = null;
		if (accept(Kind.OPN_PAREN)) {
			formalParams = formalParam();
		}
		expect(Kind.SEMI_COL);

		FuncBody funcBody = funcBody();

		expect(Kind.SEMI_COL);

		FuncDecl funcDecl = new FuncDecl(getLineNum(), funcName, formalParams,
				funcBody);
		return funcDecl;
	}

	/** formalParam = '(' [ident { ',' ident }] ')' */
	private List<Ident> formalParam() throws ParsingException {
		expect(Kind.OPN_PAREN);

		List<Ident> params = new ArrayList<Ident>();
		if (accept(Kind.IDENTIFIER)) {
			// params.add(ident());
			while (accept(Kind.COMMA)) {
				eatToken(); // eat the comma
				// params.add(ident());
			}
		}

		expect(Kind.CLS_PAREN);
		return params;
	}

	/** funcBody = { varDecl } '{' [statSequence] '}' */
	private FuncBody funcBody() throws ParsingException {
		while (accept(Kind.VAR) || accept(Kind.ARRAY)) {

		}

		expect(Kind.BEGIN);

		if (FirstSets.STATEMENT.contains(currentToken.getLexeme())) {
			statSequence();
		}

		expect(Kind.END);

		return null; 
	}

	/** typeDecl = 'var' | 'array' '[' number ']' { '[' number ']' } */
	private void typeDecl() throws ParsingException {
		if (currentToken.kind == Kind.VAR) {
			eatToken(); // eat the var token
		} else if (currentToken.kind == Kind.ARRAY) {
			eatToken(); // eat the array token
			expect(Kind.OPN_BRACK);
			number();
			expect(Kind.CLS_BRACK);

			while (accept(Kind.OPN_BRACK)) {
				eatToken(); // eat the open bracket
				number();
				expect(Kind.CLS_BRACK);
			}
		}
	}

	/** statSequence = statement { ';' statement } */
	private Result statSequence() throws ParsingException {
		Result x = statement();
		while (accept(Kind.SEMI_COL)) {
			eatToken(); // eat the semicolon
			x = statement();
		}

		return x;
	}

	/** statement = assignment | funcCall | ifStatement | whileStatement | returnStatement */
	private Result statement() throws ParsingException {
		Result result = null;
		if (accept(Kind.LET)) {
			result = assignment();
		} else if (accept(Kind.CALL)) {
			funcCall();
		} else if (accept(Kind.IF)) {
			result = ifStatement();
		} else if (accept(Kind.WHILE)) {
			whileStatement();
			// statement =
			// Statement.builder(getLineNum()).setWhileStatement(whileStatement).build();
		} else if (accept(Kind.RETURN)) {
			ReturnStatement returnStatement = returnStatement();
			// statement =
			// Statement.builder(getLineNum()).setReturnStatement(returnStatement).build();
		} else {
			throw new ParsingException();
		}

		return result;
	}

	/** assignment = 'let' designator '<-' expression */
	private Result assignment() throws ParsingException {
		expect(Kind.LET);

		Result x = designator();

		expect(Kind.BECOMES);

		Result y = expression();

		// once we write we update the instruction number, update symbols before writing
		pUtils.updateSymbols(x.varValue); 

		x = pUtils.emitAssignmentInstruction(x, y, currentBB);
		return x;
	}

	/** funcCall = 'call' ident [ '(' [expression { ',' expression } ] ')' ] */
	private Result funcCall() throws ParsingException {
		expect(Kind.CALL);

		Result funcIdent = ident();
		Result funcParams = null;

		if (accept(Kind.OPN_PAREN)) {
			eatToken(); // eat the open paren
			funcParams = expression();

			while (accept(Kind.COMMA)) {
				eatToken(); // eat the comma
				funcParams = expression();
			}
			expect(Kind.CLS_PAREN);
		}

		pUtils.createFunctionCall(currentBB, funcIdent, funcParams);

		return funcIdent;
	}

	/** ifStatement = 'if' relation 'then' statSequence [ 'else' statSequence ] 'fi' */
	private Result ifStatement() throws ParsingException {
		expect(Kind.IF);

		BasicBlock joinBlock = new BasicBlock();

		BasicBlock condBB = createBBWithControlFlow(); // create the condition
														// block
		Result relation = relation(); 
		relation.fixUp = pUtils.programCounter - 1;  //branch condition will need a location to branch to

		expect(Kind.THEN);

		BasicBlock ifBodyBB = createBBWithControlFlow();  //adds a link from condBB to ifBodyBB
		addControlFlow(ifBodyBB, joinBlock);

		Result ifBody = statSequence(); // returns the first result
		ifBody.fixUp = pUtils.programCounter - 1; //if we need to branch after else
		pUtils.fixUp(relation.fixUp); //condition will branch to else (or next instruction generated)
		pUtils.generatePhiInstructions(ifBodyBB, joinBlock, true);
		

		BasicBlock elseBodyBB = null;
		if (accept(Kind.ELSE)) {
			eatToken(); // eat the else
			currentBB = condBB; //reset as if we are coming from the condition
			elseBodyBB = createBBWithControlFlow();
			addControlFlow(elseBodyBB, joinBlock);
			Result elseBody = statSequence();
		}

		if (elseBodyBB != null) {
			if(joinBlock.getInstructions().size() > 0){
				pUtils.createUnconditionBranch(ifBodyBB, joinBlock.getInstructions().get(0));
			}
			else{
			//add branch instruction for ifbody
			pUtils.createUnconditionBranch(ifBodyBB, null); // jump to following
			}
														// block
			//currentBB = joinBlock;
		} else {
			// dont need to branch from if body, just fall through, condition will branch here
			//conditionalJumpForward(condBB, String.valueOf(pUtils.blockCount + 1));
		}

		expect(Kind.FI);

		currentBB = joinBlock;
		return relation;
	}

	/** whileStatement = 'while' relation 'do' statSequence 'od' */
	private Result whileStatement() throws ParsingException {
		// currentBB = ParserUtils.createBBWithDomRelationship(currentBB);
		expect(Kind.WHILE);
		Result relation = relation();
		expect(Kind.DO);
		// List<Statement> statSequence = statSequence();
		expect(Kind.OD);

		return null;
	}

	/**
	 * returnStatement = 'return' [ expression ]
	 * 
	 * @throws ParsingException
	 */
	private ReturnStatement returnStatement() throws ParsingException {
		expect(Kind.RETURN);

		if (accept(Kind.IDENTIFIER) || accept(Kind.NUMBER)) {
			// expression = expression();
		} else if (accept(Kind.OPN_PAREN) || accept(Kind.FUNCTION)
				|| accept(Kind.PROCEDURE)) { // identifier is not)
			eatToken(); // eat the paren, 'function', or 'procedure' token
			// expression = expression();
		}

		ReturnStatement returnStatement = new ReturnStatement(getLineNum(),
				null);
		return returnStatement;
	}

	/** designator = ident { '[' expression ']' } */
	private Result designator() throws ParsingException {
		Result x = ident();
		while (accept(Kind.OPN_BRACK)) {
			eatToken(); // eat the open bracket
			expect(Kind.CLS_BRACK);
		}
		return x;
	}

	/** expression = term { ('+' | '-') term } */
	private Result expression() throws ParsingException {
		Result result1 = term();
		if (accept(Kind.PLUS) || accept(Kind.MINUS)) {
			do {
				String op = currentToken.getLexeme();
				eatToken(); // eat the operator

				Result result2 = term();

				result1 = pUtils.combineArithmetic(result1, op, result2,
						currentBB);
			} while (accept(Kind.PLUS) || accept(Kind.MINUS));
		}
		return result1;
	}

	/** term = factor { ('*' | '/') factor } */
	private Result term() throws ParsingException {
		Result result1 = factor();
		if (accept(Kind.TIMES) || accept(Kind.DIV)) {
			do {
				String op = currentToken.getLexeme();

				eatToken(); // eat the times or div;

				Result result2 = factor();

				result1 = pUtils.combineArithmetic(result1, op, result2,
						currentBB);

			} while (accept(Kind.TIMES) || accept(Kind.DIV));
		}
		return result1;
	}

	/** relation = expression relOp expression */
	private Result relation() throws ParsingException {
		Result leftExpr = expression();

		String op = currentToken.getLexeme();
		eatToken();
		Result rightExpr = expression();

		Result x = pUtils.combineRelation(leftExpr, op, rightExpr, currentBB);

		return x;
	}

	/** factor = designator | number | '(' expression ')' | funcCall */
	private Result factor() throws ParsingException {

		if (accept(Kind.IDENTIFIER)) {
			Result x = designator();
			x.varValue = pUtils.getSymbolFromTable(x.varValue);
			return x;
		} else if (accept(Kind.NUMBER)) {
			Result x = number();
			return x;
		}

		// else if(accept(Kind.OPN_PAREN)) {
		// Expression expression = expression();
		//
		// if(expression.isNumber()) {
		// Number number = new Number(getLineNum(),
		// expression.getNumberValue());
		// factor = Factor.builder(getLineNum()).setNumber(number).build();
		// }
		// else {
		// factor =
		// Factor.builder(getLineNum()).setExpression(expression).build();
		// }
		// }
		// else if(accept(Kind.CALL)) {
		// FuncCall funcCall = funcCall();
		// factor = Factor.builder(getLineNum()).setFuncCall(funcCall).build();
		// }
		else {
			throw new ParsingException();
		}
	}

	/** ident = letter { letter | digit } */
	private Result ident() {
		LOGGER.debug("Identifier found: " + currentToken.getLexeme());
		Result x = Result.builder().type(ResultEnum.VARIABLE)
				.varValue(currentToken.getLexeme()).build();
		eatToken(); // eat the identifier
		return x;
	}

	/** number = digit {digit} */
	private Result number() {
		LOGGER.debug("Number found: " + currentToken.getLexeme());
		Result x = Result.builder().type(ResultEnum.CONSTANT)
				.constValue(Integer.valueOf(currentToken.getLexeme())).build();
		eatToken(); // eat the number
		return x;
	}

	/**
	 * Determines if the current token matches the expected token. If it does,
	 * we eat the token, otherwise the token was not what we expected during
	 * parsing and throw and error. kind the expected token kind that should be
	 * accepted
	 * 
	 */
	private void expect(Kind kind) throws ParsingException {
		if (currentToken.kind != kind) {
			throw new ParsingException(kind, currentToken);
		}
		eatToken();
	}

	/** Determines whether the current token's kind matches the formal parameter kind */
	private boolean accept(Kind kind) {
		if (currentToken.kind == kind) {
			return true;
		}
		return false;
	}

	/** Eats the current token and gets the next token */
  	private void eatToken() {
		currentToken = scanner.nextToken();
	}

  	//TODO remove this
	public int getLineNum() {
		return scanner.lineNum;
	}

	/**
	 * Creates and returns a new basic block with the flow control information
	 * from the current block to the newly created block.
	 * 
	 * @return  the newly created basic block
	 */
	private BasicBlock createBBWithControlFlow() {
		BasicBlock bb = new BasicBlock();
		addControlFlow(currentBB, bb);
		currentBB = bb;
		return bb;
	}

	/** adds a control flow entry from 'from' block to 'to' block */
	private void addControlFlow(BasicBlock from, BasicBlock to) {
		from.addControlFlow(to);
	}

	private void addDomInfo(BasicBlock dominator, BasicBlock dominatee) {
		dominator.addDominatee(dominatee);
		dominatee.addDominator(dominator);
	}

	/**  instructs the parser to print the control flow of the program in vcg format */
	public void printControlFlowToFile(String fileName){
		pUtils.printControlFlow(beginBlock, fileName);
	}
}