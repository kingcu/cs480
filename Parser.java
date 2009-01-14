//
//	parser skeleton, CS 480, Winter 2006
//	written by Tim Budd
//		modified by:
//

public class Parser {
	private Lexer lex;
	private boolean debug;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse () throws ParseException {
		lex.nextLex();
		program();
		if (lex.tokenCategory() != lex.endOfInput)
			parseError(3); // expecting end of file
	}

	private final void start (String n) {
		if (debug) System.out.println("start " + n + " token: " + lex.tokenText());
	}

	private final void stop (String n) {
		if (debug) System.out.println("recognized " + n + " token: " + lex.tokenText());
	}

	private void parseError(int number) throws ParseException {
		throw new ParseException(number);
	}

	private void program () throws ParseException {
		start("program");

		while (lex.tokenCategory() != Lexer.endOfInput) {
			declaration();
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}        
		stop("program");
	}

	// your stuff goes here
	private void declaration() {
		if (lex.match("class"))
			classDeclaration();
		else
			nonClassDeclaration();
		next.nextlex();
	}
	
	private void classDeclaration() {
		//TODO 
	}

	private void nonClassDeclaration() {
		if (lex.match("function"))
			functionDeclaration();
		else
			nonFunctionDeclaration();
	}

	private void functionDeclaration() {
		//TODO
	}
	
	private void nonFunctionDeclaration() {
		if (lex.match("var"))
			var();
		else if (lex.match("const"))
			const();
		else if (lex.match("type"))
			type();
	}

	private void var() {}
	private void const() {}
	private void type() {}

	
}
