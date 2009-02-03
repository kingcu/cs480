//
//	parser skeleton, CS 480/580, Winter 2001
//	written by Tim Budd
//		modified by: 	Cullen King <kingcu@onid.orst.edu>
//				Wojtek Rajski <rajskiw@onid.orst.edu>
//

public class Parser {
	private Lexer lex;
	private boolean debug;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse() throws ParseException {
		GlobalSymbolTable sym = new GlobalSymbolTable();
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

	private final void start (String n) {
		if (debug) System.out.println("start " + n + 
				" token: " + lex.tokenText());
	}

	private final void stop (String n) {
		if (debug) System.out.println("recognized " + n + 
				" token: " + lex.tokenText());
	}

	private void parseError(int number) throws ParseException {
		throw new ParseException(number);
	}

	private void program(SymbolTable sym) throws ParseException {
		start("program");

		while (lex.tokenCategory() != Lexer.endOfInput) {
			declaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		stop("program");
	}

	private void declaration (SymbolTable sym) throws ParseException {
		start("declaration");
		if (lex.match("class"))
			classDeclaration(sym);
		else if (lex.match("function") || lex.match("const") 
				|| lex.match("var") || lex.match("type"))
			nonClassDeclaration(sym);
		else 
			parseError(26);
		stop("declaration");
	}

	private void nonClassDeclaration (SymbolTable sym) throws ParseException {
		start("nonClassDeclaration");
		/*
		 * remove nested function declarations by removing functionDeclaration
		 * production...
		 if (lex.match("function"))
		 functionDeclaration(sym);
		 else 
		 */
		if(lex.match("const") || lex.match("var") 
				|| lex.match("type"))
			nonFunctionDeclaration(sym);
		else
			parseError(26);
		stop("nonClassDeclaration");
	}

	private void nonFunctionDeclaration (SymbolTable sym) throws ParseException {
		start("nonFunctionDeclaration");
		if (lex.match("var"))
			variableDeclaration(sym);
		else if (lex.match("const"))
			constantDeclaration(sym);
		else if (lex.match("type"))
			typeDeclaration(sym);
		else 
			parseError(26);
		stop("nonFunctionDeclaration");
	}

	private void constantDeclaration (SymbolTable sym) throws ParseException {
		start("constantDeclaration");
		String constName;

		if (lex.match("const")) {
			lex.nextLex();
			if (! lex.isIdentifier())
				parseError(27);
			constName = lex.tokenText();
			if(sym.nameDefined(constName))
				throw new ParseException(35, constName);
			lex.nextLex();
			if (! lex.match("="))
				parseError(20);
			lex.nextLex();
			if (lex.tokenCategory() == lex.intToken)
				sym.enterConstant(constName, new IntegerNode(new Integer(lex.tokenText())));
			else if (lex.tokenCategory() == lex.realToken)
				sym.enterConstant(constName, new RealNode(new Real(lex.tokenText())));;
			else if (lex.tokenCategory() == lex.stringToken)
				sym.enterConstant(constName, new StringNode(new String(lex.tokenText())));;
			else
				parseError(31);
			//sym.enterConstant(constName, new IntegerNode(new Integer(lex.tokenText())));
			lex.nextLex();
		}
		else
			parseError(6);
		stop("constantDeclaration");
	}

	private void typeDeclaration (SymbolTable sym) throws ParseException {
		start("typeDeclaration");
		String typeName;

		if (lex.match("type")) {
			lex.nextLex();
			//nameDeclaration(sym);

			start("nameDeclaration");
			if (! lex.isIdentifier()) 
				parseError(27);

			typeName = lex.tokenText();
			if(sym.nameDefined(typeName)) 
				throw new ParseException(35, typeName);

			lex.nextLex();
			if (! lex.match(":"))
				parseError(19);
			lex.nextLex();
			
			sym.enterType(typeName, type(sym));

			stop("nameDeclaration");

		} else
			parseError(14); 
		stop("typeDeclaration");
	}

	private void variableDeclaration (SymbolTable sym) throws ParseException {
		start("variableDeclaration");
		if (lex.match("var")) {
			lex.nextLex();
			nameDeclaration(sym);
		}
		else
			parseError(15);
		stop("variableDeclaration");
	}

	private void nameDeclaration (SymbolTable sym) throws ParseException {
		start("nameDeclaration");
		String nameName;

		if (! lex.isIdentifier()) 
			parseError(27);
		nameName = lex.tokenText();
		if(sym.nameDefined(nameName))
			throw new ParseException(35, nameName);
		lex.nextLex();
		if (! lex.match(":"))
			parseError(19);
		lex.nextLex();
		sym.enterVariable(nameName, type(sym));
		stop("nameDeclaration");
	}

	private void classDeclaration(SymbolTable sym) throws ParseException {
		start("classDeclaration");
		String className;
		ClassSymbolTable classSym = new ClassSymbolTable(sym);

		if (! lex.match("class"))
			parseError(5);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		className = lex.tokenText();
		if(sym.nameDefined(className))
			throw new ParseException(35, className);
		sym.enterType(className, new ClassType(sym));

		lex.nextLex();
		classBody(classSym);
		stop("classDeclaration");
	}

	private void classBody(SymbolTable sym) throws ParseException {
		start("classBody");
		if (! lex.match("begin"))
			parseError(4);
		lex.nextLex();
		while (! lex.match("end")) {
			nonClassDeclaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		lex.nextLex();
		stop("classBody");
	}

	private void functionDeclaration(SymbolTable sym) throws ParseException {
		start("functionDeclaration");
		FunctionSymbolTable funSym = new FunctionSymbolTable(sym);
		String funName;

		if (! lex.match("function"))
			parseError(10);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		funName = lex.tokenText();
		if(sym.nameDefined(funName))
			throw new ParseException(35, funName);
		lex.nextLex();
		funSym.enteringParameters(true);
		arguments(funSym);
		funSym.enteringParameters(false);
		sym.enterFunction(funName, new FunctionType(returnType(sym)));
		functionBody(funSym, funName);
		stop("functionDeclaration");
	}

	private void arguments(SymbolTable sym) throws ParseException {
		start("arguments");
		if (! lex.match("("))
			parseError(21);
		lex.nextLex();
		argumentList(sym);
		if (! lex.match(")"))
			parseError(22);
		lex.nextLex();
		stop("arguments");
	}

	private void argumentList (SymbolTable sym) throws ParseException {
		start("argumentList");
		if (lex.isIdentifier()) {
			nameDeclaration(sym);
			while (lex.match(",")) {
				lex.nextLex();
				nameDeclaration(sym);
			}
		}
		stop("argumentList");
	}

	private Type returnType(SymbolTable sym) throws ParseException {
		start("returnType");
		Type result = null;

		if (lex.match(":")) {
			lex.nextLex();
			result = type(sym);
		}
		stop("returnType");
		if(result != null)
			return result;
		else
			return PrimitiveType.VoidType;
	}

	private Type type (SymbolTable sym) throws ParseException {
		start("type");
		Type result = null;

		if (lex.isIdentifier()) {
			result = sym.lookupType(lex.tokenText());
			lex.nextLex();
		} else if (lex.match("^")) {
			lex.nextLex();
			result = new PointerType(type(sym));
		} else if (lex.match("[")) {
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			int lower = (new Integer(lex.tokenText()).intValue());
			lex.nextLex();
			if (! lex.match(":"))
				parseError(19);
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			int upper = (new Integer(lex.tokenText()).intValue());
			lex.nextLex();
			if (! lex.match("]"))
				parseError(24);
			lex.nextLex();

			result = sym.lookupType(lex.tokenText());
			//should i call type() like original, or just nextLex?
			//type(sym);
			lex.nextLex();
			result = new ArrayType(lower, upper, result);
		} else {
			parseError(30);
		}

		stop("type");
		return result;
	}

	private void functionBody (SymbolTable sym, String functionName) throws ParseException {
		start("functionBody");
		while (! lex.match("begin")) {
			nonClassDeclaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		CodeGen.genProlog(functionName, sym.size());
		compoundStatement(sym);
		CodeGen.genEpilog(functionName);
		stop("functionBody");
	}

	private void compoundStatement (SymbolTable sym) throws ParseException {
		start("compoundStatement");
		if (! lex.match("begin"))
			parseError(4);
		lex.nextLex();
		while (! lex.match("end")) {
			statement(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		lex.nextLex();
		stop("compoundStatement");
	}

	private void statement (SymbolTable sym) throws ParseException {
		start("statement");
		if (lex.match("return"))
			returnStatement(sym);
		else if (lex.match("if"))
			ifStatement(sym);
		else if (lex.match("while"))
			whileStatement(sym);
		else if (lex.match("begin"))
			compoundStatement(sym);
		else if (lex.isIdentifier())
			assignOrFunction(sym);
		else
			parseError(34);
		stop("statement");
	}

	private boolean firstExpression(SymbolTable sym) {
		if (lex.match("(") || lex.match("not") || lex.match("-") || lex.match("&"))
			return true;
		if (lex.isIdentifier())
			return true;
		if ((lex.tokenCategory() == lex.intToken) ||
				(lex.tokenCategory() == lex.realToken) ||
				(lex.tokenCategory() == lex.stringToken))
			return true;
		return false;
	}

	private void returnStatement (SymbolTable sym) throws ParseException {
		start("returnStatement");
		if (! lex.match("return"))
			parseError(12);
		lex.nextLex();
		if (lex.match("(")) {
			lex.nextLex();
			expression(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
		}
		stop("returnStatement");
	}

	private void ifStatement (SymbolTable sym) throws ParseException {
		start("ifStatement");
		if (! lex.match("if"))
			parseError(11);
		lex.nextLex();
		if (! lex.match("("))
			throw new ParseException(21);
		else
			lex.nextLex();
		expression(sym);
		if (! lex.match(")"))
			throw new ParseException(22);
		else
			lex.nextLex();
		statement(sym);
		if (lex.match("else")) {
			lex.nextLex();
			statement(sym);
		}
		stop("ifStatement");
	}

	private void whileStatement (SymbolTable sym) throws ParseException {
		start("whileStatement");
		if (! lex.match("while"))
			parseError(16);
		lex.nextLex();
		if (! lex.match("("))
			throw new ParseException(21);
		else
			lex.nextLex();
		expression(sym);
		if (! lex.match(")"))
			throw new ParseException(22);
		else
			lex.nextLex();
		statement(sym);
		stop("whileStatement");
	}

	private void assignOrFunction (SymbolTable sym) throws ParseException {
		start("assignOrFunction");
		reference(sym);
		if (lex.match("=")) {
			lex.nextLex();
			expression(sym);
		}
		else if (lex.match("(")) {
			lex.nextLex();
			parameterList(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
		}
		else
			parseError(20);
		stop("assignOrFunction");
	}

	private void parameterList (SymbolTable sym) throws ParseException {
		start("parameterList");
		if (firstExpression(sym)) {
			expression(sym);
			while (lex.match(",")) {
				lex.nextLex();
				expression(sym);
			}
		}
		stop("parameterList");
	}

	private void expression (SymbolTable sym) throws ParseException {
		start("expression");
		relExpression(sym);
		while (lex.match("and") || lex.match("or")) {
			lex.nextLex();
			relExpression(sym);
		}
		stop("expression");
	}

	private boolean relOp(SymbolTable sym) {
		if (lex.match("<") || lex.match("<=") ||
				lex.match("==") || lex.match("!=") ||
				lex.match(">") || lex.match(">="))
			return true;
		return false;
	}

	private void relExpression (SymbolTable sym) throws ParseException {
		start("relExpression");
		plusExpression(sym);
		if (relOp(sym)) {
			lex.nextLex();
			plusExpression(sym);
		}
		stop("relExpression");
	}

	private void plusExpression (SymbolTable sym) throws ParseException {
		start("plusExpression");
		timesExpression(sym);
		while (lex.match("+") || lex.match("-") || lex.match("<<")) {
			lex.nextLex();
			timesExpression(sym);
		}
		stop("plusExpression");
	}

	private void timesExpression (SymbolTable sym) throws ParseException {
		start("timesExpression");
		term(sym);
		while (lex.match("*") || lex.match("/") || lex.match("%")) {
			lex.nextLex();
			term(sym);
		}
		stop("timesExpression");
	}

	private void term (SymbolTable sym) throws ParseException {
		start("term");
		if (lex.match("(")) {
			lex.nextLex();
			expression(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
		}
		else if (lex.match("not")) {
			lex.nextLex();
			term(sym);
		}
		else if (lex.match("new")) {
			lex.nextLex();
			type(sym);
		}
		else if (lex.match("-")) {
			lex.nextLex();
			term(sym);
		}
		else if (lex.match("&")) {
			lex.nextLex();
			reference(sym);
		}
		else if (lex.tokenCategory() == lex.intToken) {
			lex.nextLex();
		}
		else if (lex.tokenCategory() == lex.realToken) {
			lex.nextLex();
		}
		else if (lex.tokenCategory() == lex.stringToken) {
			lex.nextLex();
		}
		else if (lex.isIdentifier()) {
			reference(sym);
			if (lex.match("(")) {
				lex.nextLex();
				parameterList(sym);
				if (! lex.match(")"))
					parseError(22);
				lex.nextLex();
			}
		}
		else
			parseError(33);
		stop("term");
	}

	private Ast reference(SymbolTable sym) throws ParseException {
		start("reference");
		Ast result = null;
		String refName = lex.tokenText();
		Type b;
		PointerType pb;

		if(lex.isIdentifier()) {
			result = sym.lookupName(new FramePointer(), refName);
		} else {
			parseError(27);
		}
		lex.nextLex();
		while (lex.match("^") || lex.match(".") || lex.match("[")) {
			if (lex.match("^")) {
				b = addressBaseType(result.type);
				if (!(b instanceof PointerType) )
					parseError(38);
				pb = (PointerType) b;
				result = new UnaryNode(UnaryNode.dereference,
						new AddressType(pb.baseType), result);
				lex.nextLex();
			}
			else if (lex.match(".")) {
				b = addressBaseType(result.type);
				if(!(b instanceof ClassType))
					parseError(39);
				if(!sym.nameDefined(refName))
					parseError(37);

				lex.nextLex();
				if (!lex.isIdentifier())
					parseError(27);
				result = sym.lookupName(result, lex.tokenText());
				lex.nextLex();
			}
			else {
				lex.nextLex();
				expression(sym);
				Ast indexExpression = new IntegerNode(42);
				b = addressBaseType(result.type);
				if(!(b instanceof ArrayType))
					parseError(40);
				if (!indexExpression.type.equals(PrimitiveType.IntegerType))
					parseError(41);
				indexExpression = new BinaryNode( BinaryNode.minus,
					PrimitiveType.IntegerType,
					indexExpression, new IntegerNode(at.lowerBound));
				indexExpression = new BinaryNode( BinaryNode.times,
					PrimitiveType.IntegerType,
					indexExpression, new IntegerNode(at.elementSize));  //TODO at.elementSize ?? not sure if thats right.
				//TODO add to the reference part, to form th address of the designated element
				//indexExpression = new BinaryNode( BinaryNode.plus,
				//	Type,
				//	indexExpression, new IntegerNode(at.
				if (!lex.match("]"))
					parseError(24);
				lex.nextLex();
			}
		}
		stop("reference");
		return result;
	}

	private Type addressBaseType(Type t) throws ParseException {
		if (! (t instanceof AddressType))
			parseError(37);
		AddressType at = (AddressType) t;
		return at.baseType;
	}
}
