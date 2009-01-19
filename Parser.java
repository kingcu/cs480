//
//	parser skeleton, CS 480, Winter 2006
//	written by Tim Budd
//		modified by:
//		modified by: Cullen King <kingcu@onid.orst.edu>
//		             Wojtek rajski <rajskiw@onid.orst.edu>
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
        //lex.nextlex(); //TODO don't think  we need this since we are a terminal production...
        //decleration() consumes the ';' when this is returned as a test
        stop("constantDeclaration");
    }
	
    private void typeDeclaration() throws ParseException {
        start("typeDeclaration");
        if(!lex.match("type"))
            throw new ParseException(14);
        lex.nextLex();
        nameDeclaration();
        stop("typeDeclaration");
    }

    private void variableDeclaration() throws ParseException {
        start("variableDeclaration");
        if(!lex.match("var")
            throw new ParseException(15);
        lex.nextLex();
        nameDeclaration();
        stop("variableDeclaration");
    }

    private void nameDeclaration() throws ParseException {
        start("nameDeclaration");
        if(!lex.isIdentifier())  // TODO do we have to save the identifier?
            throw new ParseException(27);
        lex.nextlex();
        type();
        stop("nameDeclaration");
    }


    /* Classes and Functions */

    private void classDeclaration() throws ParseException {
        start("classDeclaration");
        if(!lex.match("class"))
            throw new ParseException(5);
        lex.nextLex();
        if(!lex.isIdentifier())
            throw new ParseException(27);
        lex.nextLex();
        classBody();
        stop("classDeclaration");
    }

    private void classBody() throws ParseException {
        start("classBody");
        if(!lex.match("begin"))
            throw new ParseException(4);
        lex.nextLex();
        while(!lex.match("end")) {
            nonClassDeclaration();
            if(lex.match(";")
                lex.nextLex();
            else
                throw new ParseException(18);
        }
        stop("classBody");
    }

    private void functionDeclaration() throws ParseException {
        start("functionDeclaration");
            if(!lex.match("function"))
                throw new ParseException(10);
            lex.nextLex();
            if(!lex.isIdentifier())
                throw new ParseException(27);
            lex.nextLex();
            arguments();
            lex.nextLex(); // They are not or statements
            returnType();
            lex.nextLex();
            functionBody();
        stop("functionDeclaration");
    }
    //TODO not clear what arguments should do and what argumentList should do :(
    private void arguments() throws ParseException {
        start("arguments");
            if(!lex.match("(")
                throw new ParseException(21);
            while(!lex.match(")")
                argumentsList();
                if(lex.match(",")
                    lex.nextLex();
                elseTODO
                    throw new ParseException(); //TODO should this throw a comma error?
        stop("arguments");
    }

    private void argumentList() throws ParseException {
        start("argumentList");
        if(!lex.match("begin"))
            throw new ParseException(4);
        lex.nextLex();
        while(!lex.match("end")) {
            nonClassDeclaration();
            if(lex.match(";")
                lex.nextLex();
            else
                throw new ParseException(18);
        }
        stop("classBody");
                
        stop("argumentList");
    }

    private void returnType() throws ParseException {
        start("returnType");
            type();
            //TODO Add in returntype null
        stop("returnType");
    }

    private void type() throws ParseException {
        start("type");
            if(!lex.match(":")          //TODO Appears to be necessary since each type has ":" before the other stuff
                throw new ParseException(19);
            lex.nextLex();
            //TODO identifier
            //TODO ^ type
            //TODO [integer:integer] type
        stop("type");
    }

    private void functionBody() throws ParseException {
        start("functionBody");
            
        stop("functionBody");
    }

    /* Statements */

    private void compoundStatement() throws ParseException {
        start("compoundStatement");
        if(!lex.match("begin"))
            throw new ParseException(4);
        lex.nextLex();
        while(!lex.match("end")) {
            statement();
            if(lex.match(";")
                lex.nextLex();
            else
                throw new ParseException(18);
        }
        stop("compoundStatement");
    }

    private void statement() throws ParseException {
        start("statement");
        returnStatement();
        ifStatement();
        whileStatement();
        compoundState();
        assignOrFunction();
        stop("statement");
    }

    private void returnStatement() throws ParseException {
        start("returnStatement");
        if(!lex.match("return"))
            throw new ParseException(12);
        lex.nextLex();
        while(!lex.match(";"))  //TODO Check if this is right.
            expression();
        stop("returnStatement");
    }

    private void ifStatement() throws ParseException {
        start("ifStatement");
        if(!lex.match("if"))
            throw new ParseException(11);
        lex.nextLex();
        if(!lex.match("("))
            throw new ParseExcemption(21);
        lex.nextLex();
        expression();
        if(!lex.match(")"))
            throw new ParseExcemption(22);
        lex.nextLex();
        statement();
        lex.nextLex();  //TODO  Check to make sure this works right
        if(lex.match("else"))
            expression();
        stop("ifStatement");
    }

    private void elseStatement() throws ParseException {
        //TODO: this production wasn't in original grammar, so should we log them?
        // Wojtek "doesn't appear to be needed because it can be covered in ifStatement and there is no reuse value"
        //start("elseStatement");
        //stop("elseStatement");
    }

    private void whileStatement() throws ParseException {
        start("whileStatement");
        if(!lex.match("while"))  //TODO Will this funciton control the looping?
            throw new ParseException(11);
        lex.nextLex();
        if(!lex.match("("))
            throw new ParseExcemption(21);
        lex.nextLex();
        expression();
        if(!lex.match(")"))
            throw new ParseExcemption(22);
        lex.nextLex();
        statement();
        stop("whileStatement");
    }

    private void assignOrFunction() throws ParseException {
        start("assignOrFunction");
        reference();
        lex.nextLex():
        if(lex.match("="))
            expression();
        else
            parameterList();
        stop("assignOrFunction");
    }

    private void parameterList() throws ParseException {
        start("parameterList");
        expression();
        //TODO {, expression }
        //TODO null
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
        expression();
        if(lex.match("not"))
            lex.nextlex();
            term();
        if(lex.match("new"))
            lex.nextlex();
            type();
        if(lex.match("-"))
            lex.nextlex();
            term();
        reference();
        if(lex.match("&"))
            lex.nextlex();
            reference();
        //TODO reference (parameterList)
        contant();
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
