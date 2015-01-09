package pl241_compiler.components;

public class Token 
{ 
	public static final String studentName = "John Palomo";
    public static final String studentID = "60206611";
    public static final String uciNetID = "jmpalomo";	

    public static final Token EOF_TOKEN = new Token(Kind.EOF);
    public static final Token ERR_TOKEN = new Token(Kind.ERROR);

	private int lineNum;
	private int charPos;
	private Kind kind;
	private String lexeme = "";
/*	private static Map<Kind,Integer> lexemesToPrint;

	 static initializer populates the map with a list of Kind that need their
	 * associted lexemes printed when the token method toString is called
	 
	static {
		lexemesToPrint = new HashMap<Kind, Integer>();
		lexemesToPrint.put(Kind.IDENTIFIER, 1);
		lexemesToPrint.put(Kind.INTEGER, 1);
		lexemesToPrint.put(Kind.FLOAT, 1);
		lexemesToPrint.put(Kind.ERROR, 1);
	}*/

	/**
	 * This is the driving private constructor for all the static factory 
	 * token methods.  It will initialized to an error token with the string:
	 * "No lexeme given".
	 * 
	 * @param lineNum - the line number where the lexeme was found
	 * @param charPos - the starting character position of the lexeme
	 */
	private Token(int lineNum, int charPos) {
		this.lineNum = lineNum;
		this.charPos = charPos;

		// if we don't match anything, signal error
		this.kind = Kind.ERROR;
		this.lexeme = "No Lexeme Given";
	}

	private Token(Kind kind){
		this.kind = kind;
	}

	public static Token INDENTIFIER_OR_KEYWORD(String lexeme, int linePos, int charPos) {
		Token token = new Token(linePos, charPos);

		if (lexeme == null || lexeme.equals("")) {
			return token;
		}

		//assume the lexeme is an identifier
		token.lexeme = lexeme;
		token.kind = Kind.IDENTIFIER;
		
		Kind kind = findKeywordMatch(lexeme);
		if (kind != null) {
			token.lexeme = lexeme;
			token.kind = kind;
		}
		return token;
	}

	public static Token NUMBER(String num, int linePos, int charPos) {
		Token token = new Token(linePos, charPos);
		token.kind = Kind.NUMBER;
		token.lexeme = num;
		return token;
	}

	private static Kind findKeywordMatch(String lex){
		/*
		 * Following loop determines if we have a keyword lexeme.  If a keyword
		 * lexeme is found, the kind needs to be updated accordingly.  Otherwise,
		 * we have an identifier which has already been set above and the values
		 * will not get updated.
		 * 
		 */
		for(Kind kind: Kind.values()) {
			if(kind.hasStaticLexeme()) {
				if(kind.getChars().equals(lex)) {
					if( kind == Token.Kind.CALL){
							System.out.println("");
					}
					return kind;
				}
			}
		}
		return null;
	}

	public int lineNumber() {
		return lineNum;
	}
	
	public int charPosition() {
		return charPos;
	}
	
	// Return the lexeme representing or held by this token
	public int getValue() {
		return this.kind.value;
	}
	
/*	@Override
	public String toString() {
		StringBuilder tokenString = new StringBuilder(this.kind.toString());
		if(lexemesToPrint.containsKey(this.kind)){
			tokenString.append("(" + this.lexeme + ")");
		}
		tokenString.append("(lineNum:" + this.lineNum + ", charPos:" + this.charPos + ")");

		return tokenString.toString();
	}*/

	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}

	public static enum Kind 
	{
		/** Reserved Words **/
		ERROR(0),

		TIMES("*", 1),
		DIV("/", 2),

		PLUS("+", 11),
		MINUS("-", 12),

		EQL("==", 20),
		NEQ("!=", 21),
		LESS("<", 22),
		GEQ(">=", 23),
		LEQ("<=", 24),
		GRTR(">", 25),

		PERIOD(".", 30),
		COMMA(",", 31),
		OPN_BRACK("[", 32),
		CLS_BRACK("]", 34),
		CLS_PAREN(")", 35),

		BECOMES("<-", 40),
		THEN("then", 41),
		DO("do", 42),

		OPN_PAREN("(", 50),

		NUMBER(60),
		IDENTIFIER(61),

		SEMI_COL(":", 70),
		
		
		END("}", 80),
		OD("od", 81),
		FI("fi", 82),

		ELSE("else", 90),

		LET("let", 100),
		CALL("call", 101),
		IF("if", 102),
		WHILE("while", 103),
		RETURN("return", 104),

		VAR("var", 110),
		ARRAY("array", 111),
		FUNCTION("function", 112),
		PROCEDURE("procedure", 113),
				
		BEGIN("{", 150),
		MAIN("main", 200),
		EOF("255", 113);

		private String chars;
		private int value;
		
		Kind(int value) {
        	chars = null;
		}
		
		Kind(String lexeme, int value) {
        	chars = lexeme;
        	this.value = value;
		}
		
		public boolean hasStaticLexeme() {
        	return chars != null;
		}
                
		public String getChars() {
			return chars;
		}
	} 
}