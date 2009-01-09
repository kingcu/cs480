//
//		Compiler for CS 480 - Winter 2009
//		class Lexer
//
//		Written by Tim Budd, Winter term 2006
//
//		modified by: Cullen King <kingcu@onid.orst.edu>
//

import java.io.*;
import java.util.regex.*;

//
//--------Lexer----------------
//

public class Lexer {
	private PushbackReader input;
	private String token;
	private int tokenType;

	private static final String COMMENT_REGEX = "\\{.\\}";
	private static final String IDENT_REGEX = "[a-zA-Z]+\\w*";
	private static final String KEYW_REGEX = "";
	private static final String INT_REGEX = "";
	private static final String REAL_REGEX = "";
	private static final String STRING_REGEX = "";

	public Lexer(Reader in) {
		input = new PushbackReader(in);
	}

	//TODO: look at original, should i really throw IOEXception?
	//private void skipWhiteSpace() throws ParseException, IOException {
	private void skipWhiteSpace() throws ParseException {
		int c = currentChar();
		while(Character.isWhitespace((char)c)) {
			c = currentChar();
		}
		throwBack(c); //broke outta loop, toss back last char.
	}

	private int currentChar() throws ParseException {
		int cc;

		try {
			cc = input.read();
		} catch (IOException e) {
			throw new ParseException(0);
		}
		return cc;
	}

	private void throwBack(int c) throws ParseException {
		if(c != -1) {
			try {
				input.unread(c);
			} catch(IOException e) {
				throw new ParseException(0);
			}
		}
	}	

	public void nextLex() throws ParseException {
		int c;
		token = "";
		skipWhiteSpace(); //get rid of any preceding whitespaces
		c = currentChar();

		if((char)c == '{') {
			while((char)c != '}') {
				if(c == -1) //we have an unterminated comment
					throw new ParseException(1);
				c = currentChar(); //skip comments
			}
			c = currentChar();
			skipWhiteSpace();
		} 

		if(Character.isLetter((char) c)) {
			while(Character.isLetterOrDigit((char) c) && !Character.isWhitespace((char)c)) {
				token = token + (char)c;
				c = currentChar();
			}

			if(!Character.isWhitespace((char)c)) {
				throwBack(c);
			}
		} else if(Character.isDigit((char)c)) {
			token = token += (char)c;
			c = currentChar();
			int num_points = 0;
			while(Character.isDigit((char)c) || (char)c == '.') {
				if((char)c == '.')
					num_points += 1;
				token += (char)c;
				c = currentChar();
			}
			if(num_points == 0)
				tokenType = intToken;
			else if(num_points == 1)
				tokenType = realToken;
			else
				throw new ParseException(46);
			throwBack(c);
		} else if((char)c == '"') { //TODO: check no comments within comments
			c = currentChar();
			while((char)c != '"') {
				if(c == -1) //unterminated string
					throw new ParseException(2);
				token = token + (char)c;
				c = currentChar();
			}
			tokenType = stringToken;
		} else if((char)c == '<') {
			token = token + (char)c;
			c = currentChar();
			if((char)c == '<' || (char)c == '=') {
				token = token + (char)c;
			} else {
				throwBack(c);
			}
			tokenType = 6;
		} else {
			token += (char)c;
			tokenType = 6;
		}
		
		if(c == -1) { //end of input, toss to 7 so we can exit
			tokenType = 7;
		} else if(Pattern.matches(IDENT_REGEX, token)) {
			tokenType = 1;
		}
	}

	static final int identifierToken = 1;
	static final int keywordToken = 2;
	static final int intToken = 3;
	static final int realToken = 4;
	static final int stringToken = 5;
	static final int otherToken = 6;
	static final int endOfInput = 7;

	public String tokenText() {
		return token;
	}

	public int tokenCategory() {
		return tokenType;
	}

	public boolean isIdentifier() {
		return tokenType == identifierToken;
	}

	public boolean match(String test) {
		return test.equals(token);
	}
}
