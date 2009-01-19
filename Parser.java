//
//	parser skeleton, CS 480, Winter 2006
//	written by Tim Budd
//		modified by:
//

public class Parser {
	private Lexer lex;
	private boolean debug;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse() throws ParseException {
		lex.nextLex();
		program();
		if (lex.tokenCategory() != lex.endOfInput)
			parseError(3); // expecting end of file
	}

	private final void start(String n) {
		if(debug) System.out.println("start " + n + " token: " + lex.tokenText());
	}

	private final void stop(String n) {
		if(debug) System.out.println("recognized " + n + " token: " + lex.tokenText());
	}

	private void parseError(int number) throws ParseException {
		throw new ParseException(number);
	}


    /* Program Structure */

	private void program() throws ParseException {
		start("program");

		while(lex.tokenCategory() != Lexer.endOfInput) {
			declaration();
			if(lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}        
		stop("program");
	}

	private void declaration() throws ParseException {
        start("declaration");
        //classDecleration ends up with terminals {class, function, const, type, var}
        if(lex.match("function") || lex.match("const") || lex.match("type") || lex.match("var" || lex.match("class"))) {
            classDeclaration();
            nonClassDeclaration();
        } else {
            throw new ParseException(26); //TODO: might not be the correct exceptino
        }
		lex.nextLex();
        stop("declaration");
	}
	
	private void nonClassDeclaration() throws ParseException {
        start("nonClassDeclaration");
        functionDeclaration();
        nonFunctionDeclaration();
        lex.nextLex();
        stop("nonClassDeclaration");
	}

	private void nonFunctionDeclaration() throws ParseException {
        start("nonFunctionDeclaration");
        variableDeclaration();
        constantDeclaration();
        typeDeclaration();
        stop("nonFunctionDeclaration");
	}

    private void constantDeclaration() throws ParseException {
        start("constantDeclaration");
        if(!lex.match("const"))
            throw new ParseException(6);
        lex.nextLex();
        if(!lex.isIdentifier())
            throw new ParseException(27);
        lex.nextLex();
        if(!lex.match("="))
            throw new ParseException(20); //TODO: i think this is the wrong code.  check againset buds using test100
        lex.nextLex();
        int tid = lex.tokenCategory();
        if(tid != Lexer.stringToken || tid != Lexer.realToken || tid != Lexer.intToken)
            throw new ParseException(31);
        //lex.nextlex(); //don't think  we need this since we are a terminal production...
        //decleration() consumes the ';' when this is returned as a test
        stop("constantDeclaration");
    }
	
    private void typeDeclaration() throws ParseException {
        start("typeDeclaration");
        stop("typeDeclaration");
    }

    private void variableDeclaration() throws ParseException {
        start("variableDeclaration");
        stop("variableDeclaration");
    }

    private void nameDeclaration() throws ParseException {
        start("nameDeclaration");
        stop("nameDeclaration");
    }


    /* Classes and Functions */

	private void classDeclaration() throws ParseException {
        start("classDeclaration");
        stop("classDeclaration");
	}

    private void classBody() throws ParseException {
        start("classBody");
        stop("classBody");
    }

	private void functionDeclaration() throws ParseException {
        start("functionDeclaration");
        stop("functionDeclaration");
	}

    private void arguments() throws ParseException {
        start("arguments");
        stop("arguments");
    }

    private void argumentList() throws ParseException {
        start("argumentList");
        stop("argumentList");
    }

    private void returnType() throws ParseException {
        start("returnType");
        stop("returnType");
    }

    private void type() throws ParseException {
        start("type");
        stop("type");
    }

    private void functionBody() throws ParseException {
        start("functionBody");
        stop("functionBody");
    }

    /* Statements */

    private void compoundStatement() throws ParseException {
        start("compoundStatement");
        stop("compoundStatement");
    }

    private void statement() throws ParseException {
        start("statement");
        stop("statement");
    }

    private void returnStatement() throws ParseException {
        start("returnStatement");
        stop("returnStatement");
    }

    private void ifStatement() throws ParseException {
        start("ifStatement");
        stop("ifStatement");
    }

    private void elseStatement() throws ParseException {
        //TODO: this production wasn't in original grammar, so should we log them?
        //start("elseStatement");
        //stop("elseStatement");
    }

    private void whileStatement() throws ParseException {
        start("whileStatement");
        stop("whileStatement");
    }

    private void assignOrFunction() throws ParseException {
        start("assignOrFunction");
        stop("assignOrFunction");
    }

    private void parameterList() throws ParseException {
        start("parameterList");
        stop("parameterList");
    }

    /* Expressions */

    private void expression() throws ParseException {
        start("expression");
        stop("expression");
    }

    private void relExpression() throws ParseException {
        start("relExpression");
        stop("relExpression");
    }

    private void plusExpression() throws ParseException {
        start("plusExpression");
        stop("plusExpression");
    }

    private void timesExpression() throws ParseException {
        start("timesExpression");
        stop("timesExpression");
    }

    private void term() throws ParseException {
        start("term");
        stop("term");
    }

    private void reference() throws ParseException {
        start("reference");
        stop("reference");
    }

    private void referencePrime() throws ParseException {
        //TODO: should this call start and stop?  probably not...
    }
}
