//
//	parser skeleton, CS 480, Winter 2006
//	written by Tim Budd
//
//  modified by: Cullen King <kingcu@onid.orst.edu>
//	         Wojtek Rajski <rajskiw@onid.orst.edu>
//

public class Parser {
	private Lexer lex;
	private boolean debug;
	private GlobalSymbolTable sym;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse() throws ParseException {
		sym = new GlobalSymbolTable();
		sym.enterType("int", PrimitiveType.IntegerType);
		sym.enterType("real", PrimitiveType.RealType);
		sym.enterFunction("printInt", new FunctionType(PrimitiveType.VoidType));
		sym.enterFunction("printReal", new FunctionType(PrimitiveType.VoidType));
		sym.enterFunction("printStr", new FunctionType(PrimitiveType.VoidType));
		lex.nextLex();
		program(sym);
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

	private void program(SymbolTable sym) throws ParseException {
		start("program");
		while(lex.tokenCategory() != Lexer.endOfInput) {
			declaration(sym);
			if(lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}        
		stop("program");
	}

	private void declaration(SymbolTable sym) throws ParseException {
        start("declaration");
        //classDecleration ends up with terminals {class, function, const, type, var}
        if(lex.match("function") || lex.match("const") || lex.match("type") || lex.match("var")) {
            nonClassDeclaration(sym);
        } else if(lex.match("class")) {
            classDeclaration(sym);
        } else {
            throw new ParseException(26); //TODO: might not be the correct exception
        }
		//lex.nextLex();
        stop("declaration");
	}
	
	private void nonClassDeclaration(SymbolTable sym) throws ParseException {
        start("nonClassDeclaration");
        if(lex.match("function")) {
            functionDeclaration(sym);
		} else if(lex.match("var") || lex.match("const") || lex.match("type")) {
            nonFunctionDeclaration(sym);
        } else {
            throw new ParseException(26); //TODO: probably not correct exception
        }
        stop("nonClassDeclaration");
	}

	private void nonFunctionDeclaration(SymbolTable sym) throws ParseException {
        start("nonFunctionDeclaration");
        if(lex.match("var"))
            variableDeclaration(sym);
		else if(lex.match("const"))
            constantDeclaration(sym);
		else if(lex.match("type"))
            typeDeclaration(sym);
        else
            throw new ParseException(26); //TODO: probbly wrong exception
        stop("nonFunctionDeclaration");
	}

    private void constantDeclaration(SymbolTable sym) throws ParseException {
        start("constantDeclaration");
        if(!lex.match("const"))
            throw new ParseException(6);
        lex.nextLex();
        if(!lex.isIdentifier())
            throw new ParseException(27);
        lex.nextLex();
        if(!lex.match("="))
            throw new ParseException(20); //TODO: i think this is the wrong exception.  check againset buds using test100
        lex.nextLex();
        int tid = lex.tokenCategory();
        if(tid != Lexer.stringToken || tid != Lexer.realToken || tid != Lexer.intToken)
            throw new ParseException(31);
        lex.nextLex();
        if(sym.nameDefined(lex.tokenText()))
			throw new ParseException(35, lex.tokenText());
        //TODO enterConstant
        stop("constantDeclaration");
    }
	
    private void typeDeclaration(SymbolTable sym) throws ParseException {
        start("typeDeclaration");
        if(!lex.match("type"))
            throw new ParseException(14);
        lex.nextLex();

		//Instead of calling nameDeclaration, put body of it inline
        start("nameDeclaration");
        if(!lex.isIdentifier())  // TODO do we have to save the identifier?
            throw new ParseException(27);
        lex.nextLex();
        if(!lex.match(":"))
            throw new ParseException(19); //need a colon between identifier and type
        lex.nextLex();
        type(sym);
		stop("nameDeclaration");

        stop("typeDeclaration");
    }

    private void variableDeclaration(SymbolTable sym) throws ParseException {
        start("variableDeclaration");
        if(!lex.match("var"))
            throw new ParseException(15);
        lex.nextLex();
        nameDeclaration(sym);
        stop("variableDeclaration");
    }

    private void nameDeclaration(SymbolTable sym) throws ParseException {
        start("nameDeclaration");
        if(!lex.isIdentifier())  // TODO do we have to save the identifier?
            throw new ParseException(27);
        lex.nextLex();
        if(!lex.match(":"))
            throw new ParseException(19); //need a colon between identifier and type
        lex.nextLex();
        type(sym);
        stop("nameDeclaration");
    }


    /* Classes and Functions */

    private void classDeclaration(SymbolTable sym) throws ParseException {
        start("classDeclaration");
        if(!lex.match("class"))
            throw new ParseException(5);
        lex.nextLex();
        if(!lex.isIdentifier())
            throw new ParseException(27);
        lex.nextLex();
        classBody(sym);
        stop("classDeclaration");
    }

    private void classBody(SymbolTable sym) throws ParseException {
        start("classBody");
        if(!lex.match("begin"))
            throw new ParseException(4);
        lex.nextLex();
        while(!lex.match("end")) {
            nonClassDeclaration(sym);
            if(!lex.match(";"))
                throw new ParseException(18);
            lex.nextLex();
        }
        lex.nextLex(); //we end with a terminal, so advance token for next state
        stop("classBody");
    }

    private void functionDeclaration(SymbolTable sym) throws ParseException {
        start("functionDeclaration");
        if(!lex.match("function"))
            throw new ParseException(10);
        lex.nextLex();
        if(!lex.isIdentifier())
            throw new ParseException(27);
        lex.nextLex();
        arguments(sym);
        returnType(sym);
        functionBody(sym);
        stop("functionDeclaration");
    }

    //TODO not clear what arguments should do and what argumentList should do :(
    private void arguments(SymbolTable sym) throws ParseException {
        start("arguments");
        if(!lex.match("("))
            throw new ParseException(21);
        lex.nextLex();
        argumentList(sym);
		if(!lex.match(")"))
			throw new ParseException(22);
		lex.nextLex();
        stop("arguments");
    }

    private void argumentList(SymbolTable sym) throws ParseException {
        start("argumentList");
		if(lex.isIdentifier()) {
			while(!lex.match(")")) {
				nameDeclaration(sym);
				if(lex.match(","))
					lex.nextLex();
			}
		}
        stop("argumentList");
    }

    private Type returnType(SymbolTable sym) throws ParseException {
        start("returnType");
        if(lex.match(":")) {
            lex.nextLex();
            return type(sym);
        }
        stop("returnType");
        return PrimitiveType.VoidType;
    }

    private Type type(SymbolTable sym) throws ParseException {
        start("type");
        Type result = null;

        if(lex.isIdentifier()) {
            result = sym.lookupType(lex.tokenText());
            lex.nextLex(); //we are a terminal, so call nextLex to setup next state
        } else if(lex.match("^")) {
            lex.nextLex();
            result = new PointerType(type(sym));
        } else if(lex.match("[")) {
            lex.nextLex();
            if(lex.tokenCategory() != Lexer.intToken)
                throw new ParseException(32); //expected an integer
            int lower = (new Integer(lex.tokenText()).intValue());
            lex.nextLex();

            if(!lex.match(":"))
                throw new ParseException(19); //needed to see :
            lex.nextLex();

            if(lex.tokenCategory() != Lexer.intToken)
                throw new ParseException(32); //expected an integer
            int upper = (new Integer(lex.tokenText()).intValue());
            lex.nextLex();

            if(!lex.match("]"))
                throw new ParseException(24); //should have seen ]
            lex.nextLex();

			result = sym.lookupType(lex.tokenText());
            //type(sym); //might use this instead of nextLex();
			//gotta advance so we are at next token
			lex.nextLex();
            result = new ArrayType(lower, upper, result);
        } else {
		   throw new ParseException(30); //we expected a type name
		}	   
        stop("type");
        return result;
    }

    private void functionBody(SymbolTable sym) throws ParseException {
        start("functionBody");
        while(!lex.match("begin")) {
            //nonClassDeclaration(sym);
			//don't want nested functions, so we call nonFunctionDeclaration directly,
			//since nonClassDeclaration called just function and nonFunctionDeclaration.
			nonFunctionDeclaration(sym);
            if(!lex.match(";"))
                throw new ParseException(18); //expecting semicolon
            lex.nextLex();
        }
        compoundStatement(sym);
        stop("functionBody");
    }

    /* Statements */

    private void compoundStatement(SymbolTable sym) throws ParseException {
        start("compoundStatement");
        if(!lex.match("begin"))
            throw new ParseException(4);
        lex.nextLex();
        while(!lex.match("end")) {
            statement(sym);
            if(!lex.match(";"))
                throw new ParseException(18);
            lex.nextLex();
        }
		lex.nextLex();
        stop("compoundStatement");
    }

    private void statement(SymbolTable sym) throws ParseException {
        start("statement");
		if(lex.match("return")) {
	        returnStatement(sym);
		} else if(lex.match("if")) {
	        ifStatement(sym);
		} else if(lex.match("while")) {
	        whileStatement(sym);
		} else if(lex.match("begin")) {
	        compoundStatement(sym);
		} else if(lex.isIdentifier()) {
	        assignOrFunction(sym);
		} else {
			throw new ParseException(34); //expecting statement
		}
        stop("statement");
    }

    private void returnStatement(SymbolTable sym) throws ParseException {
        start("returnStatement");
        if(!lex.match("return"))
            throw new ParseException(12);
        lex.nextLex();
        if(lex.match("(")) {
			lex.nextLex();
            expression(sym);
			if(!lex.match(")"))
				throw new ParseException(22);
			lex.nextLex();
		}
        stop("returnStatement");
    }

    private void ifStatement(SymbolTable sym) throws ParseException {
        start("ifStatement");
        if(!lex.match("if"))
            throw new ParseException(11);
        lex.nextLex();
        if(!lex.match("("))
            throw new ParseException(21);
        lex.nextLex();
        expression(sym);
        if(!lex.match(")"))
            throw new ParseException(22);
        lex.nextLex();
        statement(sym);
        if(lex.match("else")) {
			lex.nextLex();
            statement(sym);
		}
        stop("ifStatement");
    }

    private void whileStatement(SymbolTable sym) throws ParseException {
        start("whileStatement");
        if(!lex.match("while"))  //TODO Will this funciton control the looping?
            throw new ParseException(11);
        lex.nextLex();
        if(!lex.match("("))
            throw new ParseException(21);
        lex.nextLex();
        expression(sym);
        if(!lex.match(")"))
            throw new ParseException(22);
        lex.nextLex();
        statement(sym);
        stop("whileStatement");
    }

    private void assignOrFunction(SymbolTable sym) throws ParseException {
        start("assignOrFunction");
        reference(sym);
        if(lex.match("=")) {
			lex.nextLex();
            expression(sym);
		} else if(lex.match("(")) {
			lex.nextLex();
            parameterList(sym);
			if(!lex.match(")")) {
				throw new ParseException(22); //expected closeing paren
			}
			lex.nextLex();
		}
        stop("assignOrFunction");
    }

    private void parameterList(SymbolTable sym) throws ParseException {
        start("parameterList");
		int tid = lex.tokenCategory();
		if(lex.match("not") || lex.match("new") || lex.match("(") || lex.match("-") || lex.match("&") || lex.isIdentifier() || tid == Lexer.realToken || tid == Lexer.intToken || tid == Lexer.stringToken) {
			expression(sym);
			while(lex.match(",")) {
				lex.nextLex();
				expression(sym);
			}
		}
        stop("parameterList");
    }

    /* Expressions */

    private void expression(SymbolTable sym) throws ParseException {
        start("expression");
		relExpression(sym);
		while(lex.match("and") || lex.match("or")) {
			lex.nextLex();
			relExpression(sym);
		}
        stop("expression");
    }

    private void relExpression(SymbolTable sym) throws ParseException {
        start("relExpression");
		plusExpression(sym);
		while(lex.match("<") || lex.match("<=") || lex.match("!=") || lex.match("==") || lex.match(">=") || lex.match(">")) {
			lex.nextLex();
			plusExpression(sym);
		}
        stop("relExpression");
    }

    private void plusExpression(SymbolTable sym) throws ParseException {
        start("plusExpression");
		timesExpression(sym);
		while(lex.match("+") || lex.match("-") || lex.match("<<")) {
			lex.nextLex();
			timesExpression(sym);
		}
        stop("plusExpression");
    }

    private void timesExpression(SymbolTable sym) throws ParseException {
        start("timesExpression");
		term(sym);
		while(lex.match("*") || lex.match("/") || lex.match("%")) {
			lex.nextLex();
			term(sym);
		}
        stop("timesExpression");
    }

    private void term(SymbolTable sym) throws ParseException {
        start("term");
		int tid = lex.tokenCategory();

		if(lex.match("(")) {
			lex.nextLex();
			expression(sym);
			if(!lex.match(")"))
				throw new ParseException(22);
			lex.nextLex();
		} else if(lex.match("not")) {
            lex.nextLex();
            term(sym);
        } else if(lex.match("new")) {
            lex.nextLex();
            type(sym);
        } else if(lex.match("-")) {
            lex.nextLex();
            term(sym);
        } else if(lex.match("&")) {
            lex.nextLex();
            reference(sym);
        } else if(lex.isIdentifier()) {
			reference(sym);
			if(lex.match("(")) {
				lex.nextLex();
				parameterList(sym);
				if(!lex.match(")"))
					throw new ParseException(22);
				lex.nextLex();
			}
		} else if(tid == Lexer.intToken || tid == Lexer.realToken || tid == Lexer.stringToken) {
			lex.nextLex();
		} else {
			throw new ParseException(33); //TODO: is this right?!?!
		}
        stop("term");
    }

    private void reference(SymbolTable sym) throws ParseException {
        start("reference");
		if(!lex.isIdentifier()) {
			throw new ParseException(27); //expected an identifier
		}
		lex.nextLex();
		referencePrime(sym);
        stop("reference");
    }

    private void referencePrime(SymbolTable sym) throws ParseException {
        //TODO: should this call start and stop?  probably not...
		if(lex.match(".")) {
			lex.nextLex();
			if(!lex.isIdentifier()) {
				throw new ParseException(27);
			}
			lex.nextLex();
		} else if(lex.match("[")) {
			lex.nextLex();
			expression(sym);
			if(!lex.match("]")) {
				throw new ParseException(23); //expected left bracket
			}
			lex.nextLex();
		} else if(lex.match("^")) {
			//do nothing, since it's just a terminal
			lex.nextLex();
		}
    }
}
