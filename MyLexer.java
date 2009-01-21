//
//		Compiler for CS 480 - Winter 2009
//		class Lexer
//
//		Written by Tim Budd, Winter term 2006
//
//		modified by: Cullen King <kingcu@onid.orst.edu>
//		             Wojtek rajski
//

import java.io.*;
import java.util.regex.*;
import java.util.Hashtable;

//
//--------Lexer----------------
//

public class Lexer {
	private PushbackReader input;
	private String token;
	private int tokenType;
	private Hashtable<String, Boolean> keywords;

	private static final String COMMENT_REGEX = "\\{.\\}";
	private static final String IDENT_REGEX = "[a-zA-Z]+\\w*";
    private static final String CTL_CHAR_REGEX = "[<>!=]";

	public Lexer(Reader in) {
		input = new PushbackReader(in);
		//no rehashes if initial capacity is greater than
		//number of entries divided by load factor (default .75)
		//we have 20 keywords, so make our capacity 28
		keywords = new Hashtable<String, Boolean>(28);
		populateKeywords();
	}

	private void populateKeywords() {
		String[] values = {"and", "or", "new", "not", "else", "if", "while", "return", "begin", "end", "int", "real", "class", "function", "var", "type", "const"};
		for(String value : values) {
			keywords.put(value, true);
		}
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

	//TODO: look at original, should i really throw IOEXception?
	//private void skipWhiteSpace() throws ParseException, IOException {
	private void skipWhiteSpace() throws ParseException {
		int c = currentChar();
		while(Character.isWhitespace((char)c)) {
			c = currentChar();
		}
		throwBack(c); //broke outta loop, toss back last char.
	}

	private void skipComment() throws ParseException {
		int c = currentChar();
		if((char)c == '{') {
			while((char)c != '}') {
				if(c == -1) //we have an unterminated comment
					throw new ParseException(1);
				c = currentChar(); //skip comments
			}
			skipWhiteSpace();
			skipComment(); //run again, make sure more than one comment is skipped
		} else {
		 throwBack(c);
		}	 
	}

	public void nextLex() throws ParseException {
		int c;
		token = "";
		skipWhiteSpace(); //get rid of any preceding whitespaces
		skipComment(); //get rid of any comments maybe lying around
		
		c = currentChar();
		if((char)c == '"') { //TODO: check no comments within comments
			c = currentChar();
			while((char)c != '"') {
				if(c == -1) //unterminated string
					throw new ParseException(2);
				token = token + (char)c;
				c = currentChar();
			}
			c = currentChar(); //eat up the close quote
			tokenType = stringToken;
		} else if(Character.isDigit((char)c)) {
			int num_points = 0;
			while(Character.isDigit((char)c) || (char)c == '.') {
				if((char)c == '.')
					num_points++;
				token += (char)c;
				c = currentChar();
			}
			tokenType = intToken;
			if(num_points > 0)
				tokenType = realToken;
		} else if(!Character.isLetterOrDigit((char)c) && c != -1) {
			token += (char)c;
			if(Pattern.matches(CTL_CHAR_REGEX, Character.toString((char)c))) {
				int last_c = c;
				c = currentChar();
				if((char)c == '=')
					token += (char)c;
				else if((char)c == '<' && (char)last_c == '<')
					token += (char)c;
				else
					throwBack(c);
			}
			c = currentChar(); //eat next token so our throwBack(c) down below doesn't add used char back
			tokenType = otherToken;
		} else if(c != -1) {
			token += (char)c;
			c = currentChar();
			while(Character.isLetterOrDigit((char)c)) {
				token = token + (char)c;
				c = currentChar();
			}
			if(keywords.containsKey(token)) 
				tokenType = keywordToken;
			else if(Pattern.matches(IDENT_REGEX, token))
				tokenType = identifierToken;
			else 
				tokenType = otherToken;
		}
		throwBack(c);

		if(c == -1) { //end of input, toss to 7 so we can exit
			tokenType = endOfInput;
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
