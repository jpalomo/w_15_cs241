package compiler.components.lex;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class Scanner
{
	static Logger LOGGER = LoggerFactory.getLogger(Scanner.class);

	private FileReader fileReader;

	public Token token;
	public char inputSym = ' '; //holds the character from file reader

	public Scanner(String fileName) {
		Preconditions.checkArgument(StringUtils.isNotEmpty(fileName), "the input file loction cannot be empty or null");
		fileReader = new FileReader(fileName);
	} 

	public int nextToken() {
		while(Character.isWhitespace(inputSym)) {
			next();
		} 
		
		if(inputSym == FileReader.EOF) {
			LOGGER.debug("Found EOF token");
			token = Token.EOF_TOKEN;
		}
		else if(inputSym == FileReader.ERROR) {  //error 
			LOGGER.debug("Found ERROR token");
			token = Token.ERR_TOKEN;
		} 
		else if(Character.isLowerCase(inputSym)){
			identifierOrKeyword(); 
		} 
		else if(Character.isDigit(inputSym)) {
			number();
		}
		else {
			relOpOrKeyword();
		}
		return token.getValue();
	}

	private void identifierOrKeyword() {
		StringBuilder lexeme = new StringBuilder();
		lexeme.append(inputSym);
		next();	//consume the character
		while(isChar() || isNumber()) {
			lexeme.append(inputSym);
			next();
		} 
        LOGGER.info("Identifier/Keyword Lexeme: " + lexeme.toString()); 
		token = Token.INDENTIFIER_OR_KEYWORD(lexeme.toString());
	}

	private boolean isChar() {
		if(inputSym >= 'a' && inputSym <= 'z'){
			return true;
		}
		return false;
	}

	private void number() {
		StringBuilder lexeme = new StringBuilder();
		lexeme.append(inputSym);
		next();	//consume the character
		while(isNumber()) {
			lexeme.append(inputSym);
			next();
		} 
        LOGGER.info("Number Lexeme: " + lexeme.toString()); 
		token = Token.NUMBER(lexeme.toString()); 
	}

	private boolean isNumber() {
		if(inputSym >= '0' && inputSym <= '9'){
			return true;
		}
		return false;
	}

	private void relOpOrKeyword() {
		StringBuilder lexeme = new StringBuilder();
		lexeme.append(inputSym);
		next();	//consume the character
		while(isOtherChar()) {
			lexeme.append(inputSym);
			next();
		} 
        LOGGER.info("RelOp or Keyword Lexeme: " + lexeme.toString()); 
		token = Token.RELOP_OR_KEYWORD(lexeme.toString()); 
	}

	private boolean isOtherChar() {
		if(!Character.isWhitespace(inputSym) && inputSym != FileReader.EOF && inputSym != FileReader.ERROR){
			return true;
		}
		return false;
	}

	public void error(String errorMsg) {
		fileReader.error(errorMsg);
	}

	/**
	 * Has the file reader return the current symbol to be consumed. * 
	 */
	private void next() {
		inputSym = fileReader.getSym();
		LOGGER.trace("Retrieved: " + inputSym); 
	} 

	public Token getToken(){
		return token;
	}
}