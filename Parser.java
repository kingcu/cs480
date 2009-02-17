//
//	parser skeleton, CS 480/580, Winter 1998
//	written by Tim Budd
//		modified by:
//

public class Parser {
	private Lexer lex;
	private boolean debug;

	public Parser (Lexer l, boolean d) { lex = l; debug = d; }

	public void parse () throws ParseException {
		lex.nextLex();
		SymbolTable sym = new GlobalSymbolTable();
		sym.enterType("int", PrimitiveType.IntegerType);
		sym.enterType("real", PrimitiveType.RealType);
		sym.enterFunction("printInt", new FunctionType(PrimitiveType.VoidType));
		sym.enterFunction("printReal", new FunctionType(PrimitiveType.VoidType));
		sym.enterFunction("printStr", new FunctionType(PrimitiveType.VoidType));
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

	private void program (SymbolTable sym) throws ParseException {
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
		if (lex.match("function"))
			functionDeclaration(sym);
		else if (lex.match("const") || lex.match("var") 
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
		if (lex.match("const")) {
			lex.nextLex();
			if (! lex.isIdentifier())
				parseError(27);
			String name = lex.tokenText();
			if (sym.nameDefined(name))
				throw new ParseException(35, name);
			lex.nextLex();
			if (! lex.match("="))
				parseError(20);
			lex.nextLex();
			Ast value = null;
			if (lex.tokenCategory() == lex.intToken)
				value = new IntegerNode(new Integer(lex.tokenText()));
			else if (lex.tokenCategory() == lex.realToken)
				value = new RealNode(new Double(lex.tokenText()));
			else if (lex.tokenCategory() == lex.stringToken)
				value = new StringNode(lex.tokenText());
			else
				parseError(31);
			sym.enterConstant(name, value);
			lex.nextLex();
			}
		else
			parseError(6);
		stop("constantDeclaration");
		}

	private void typeDeclaration (SymbolTable sym) throws ParseException {
		start("typeDeclaration");
		if (lex.match("type")) {
			lex.nextLex();
			if (! lex.isIdentifier())
				parseError(27);
			String name = lex.tokenText();
			if (sym.nameDefined(name))
				throw new ParseException(35, name);
			lex.nextLex();
			if (! lex.match(":"))
				parseError(19);
			lex.nextLex();
			sym.enterType(name, type(sym));
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
		if (! lex.isIdentifier()) 
			parseError(27);
		String name = lex.tokenText();
		if (sym.nameDefined(name))
			throw new ParseException(35, name);
		lex.nextLex();
		if (! lex.match(":"))
			parseError(19);
		lex.nextLex();
		sym.enterIdentifier(name, type(sym));
		stop("nameDeclaration");
		}

	private void classDeclaration(SymbolTable sym) throws ParseException {
		start("classDeclaration");
		if (! lex.match("class"))
			parseError(5);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		String name = lex.tokenText();
		if (sym.nameDefined(name))
			throw new ParseException(35, name);
		lex.nextLex();
		SymbolTable csym = new ClassSymbolTable(sym);
		sym.enterType(name, new ClassType(csym));
		classBody(csym);
		stop("classDeclaration");
		}

	private void classBody(SymbolTable sym) throws ParseException {
		start("classBody");
		if (! lex.match("begin"))
			parseError(4);
		lex.nextLex();
		while (! lex.match("end")) {
			nonFunctionDeclaration(sym);
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
		if (! lex.match("function"))
			parseError(10);
		lex.nextLex();
		if (! lex.isIdentifier())
			parseError(27);
		String name = lex.tokenText();
		if (sym.nameDefined(name))
			throw new ParseException(35, name);
		lex.nextLex();
		FunctionSymbolTable fsym = new FunctionSymbolTable(sym);
		arguments(fsym);
		fsym.doingArguments = false;
		Type rt = returnType(sym);
		sym.enterFunction(name, new FunctionType(rt));
		functionBody(fsym, name);
		stop("functionDeclaration");
		}
		
	private void arguments (SymbolTable sym) throws ParseException {
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

	private Type returnType (SymbolTable sym) throws ParseException {
		start("returnType");
		Type result = PrimitiveType.VoidType;
		if (lex.match(":")) {
			lex.nextLex();
			result = type(sym);
			}
		stop("returnType");
		return result;
		}

	private Type type (SymbolTable sym) throws ParseException {
		start("type");
		Type result = null;
		if (lex.isIdentifier()) {
			result = sym.lookupType(lex.tokenText());
			lex.nextLex();
			}
		else if (lex.match("^")) {
			lex.nextLex();
			result = new PointerType(type(sym));
			}
		else if (lex.match("[")) {
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			int lower = (new Integer(lex.tokenText())).intValue();
			lex.nextLex();
			if (! lex.match(":"))
				parseError(19);
			lex.nextLex();
			if (lex.tokenCategory() != lex.intToken)
				parseError(32);
			int upper = (new Integer(lex.tokenText())).intValue();
			lex.nextLex();
			if (! lex.match("]"))
				parseError(24);
			lex.nextLex();
			result = new ArrayType(lower, upper, type(sym));
			}
		else
			parseError(30);
		stop("type");
		return result;
		}

	private void functionBody (SymbolTable sym, String name) throws ParseException {
		start("functionBody");
		while (! lex.match("begin")) {
			nonFunctionDeclaration(sym);
			if (lex.match(";"))
				lex.nextLex();
			else
				throw new ParseException(18);
		}
		CodeGen.genProlog(name, sym.size());
		compoundStatement(sym);
		CodeGen.genEpilog(name);
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

	private boolean firstExpression() {
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
		Ast val = reference(sym);
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
		if (firstExpression()) {
			expression(sym);
			while (lex.match(",")) {
				lex.nextLex();
				expression(sym);
				}
			}
		stop("parameterList");
		}
		
	private boolean MustBeBoolean (Ast value) throws ParseException {
		if (value.type == PrimitiveType.BooleanType)
			return true;
		parseError(43);
		return false;
		}

	private Ast expression (SymbolTable sym) throws ParseException {
		start("expression");
		Ast result = relExpression(sym);
		while (lex.match("and") || lex.match("or")) {
			lex.nextLex();
			Ast argument2 = relExpression(sym);
			if (MustBeBoolean (result) && MustBeBoolean (argument2)){
				if (lex.match("and"))
					result = new BinaryNode(BinaryNode.and,
							PrimitiveType.BooleanType, result, argument2);
				else if (lex.match("or"))
					result = new BinaryNode(BinaryNode.or,
							PrimitiveType.BooleanType, result, argument2);
				}
			}
		stop("expression");
		return result;
		}

	private boolean relOp() {
		if (lex.match("<") || lex.match("<=") ||
			lex.match("==") || lex.match("!=") ||
				lex.match(">") || lex.match(">="))
				return true;
		return false;
		}

	private Ast relExpression (SymbolTable sym) throws ParseException {
		start("relExpression");
		Ast left = plusExpression(sym);
		Ast right = null;
		Ast result = null;
		String op = null;
		int nodeType = 0;
		if (relOp()) {
			op = lex.tokenText();
			lex.nextLex();
			right = plusExpression(sym);
			if(left.type == right.type) {
				if(op.equals(">")) {
					nodeType = BinaryNode.greater;
				} else if(op.equals("<")) {
					nodeType = BinaryNode.less;
				} else if(op.equals("==")) {
					nodeType = BinaryNode.equal;
				} else if(op.equals("<=")) {
					nodeType = BinaryNode.lessEqual;
				} else if(op.equals(">=")) {
					nodeType = BinaryNode.greaterEqual;
				} else if(op.equals("!=")) {
					nodeType = BinaryNode.notEqual;
				}
				result = new BinaryNode(nodeType, PrimitiveType.BooleanType,
					left, right);
			} else
				parseError(44);
		}
		stop("relExpression");
		return result;
	}

	private Ast convertToReal(Ast thingamajig) {
		return new UnaryNode(UnaryNode.convertToReal, PrimitiveType.RealType, thingamajig);
	}

	//TODO: may have the order of things wrong, his statements are ambiguous.
	//will test further and come back here if there are issues.
	private Ast plusExpression (SymbolTable sym) throws ParseException {
		start("plusExpression");
		Ast result = timesExpression(sym);
		Ast right = null;
		String op = lex.tokenText();
		int nodeType = 0;
		Type resultType = null;
		Type rt = PrimitiveType.RealType;
		Type it = PrimitiveType.IntegerType;
		while (lex.match("+") || lex.match("-") || lex.match("<<")) {
			lex.nextLex();
			right = timesExpression(sym);

			if(op.equals("+")) {
				if(right.type == rt || result.type == rt) {
					result = convertToReal(result);
					right = convertToReal(right);
					resultType = rt;
				} else {
					resultType = it;
				}
				nodeType = BinaryNode.plus;
			} else if(op.equals("-")) {
				if(right.type == rt || result.type == rt) {
					result = convertToReal(result);
					right = convertToReal(right);
					resultType = rt;
				} else {
					resultType = it;
				}
				nodeType = BinaryNode.minus;
			} else if(op.equals("<<")) {
				if(result.type != it || right.type != it)
					parseError(41);
				nodeType = BinaryNode.leftShift;
				resultType = it; //int left shifted by an int is another int
			}
			if((right.type != it || right.type != rt) || 
					((result.type != it || result.type != rt))) {
				parseError(46);
			}
			if(right.type != result.type)
				parseError(44);
			//ok past all the tests...do the magic
			result = new BinaryNode(nodeType, resultType, result, right);
		}
		stop("plusExpression");
		return result;
	}

	//TODO: same as for plusExpression...may be incorrect ordering.
	private Ast timesExpression (SymbolTable sym) throws ParseException {
		start("timesExpression");
		Ast result = term(sym);
		Ast right = null;
		String op = lex.tokenText();
		int nodeType = 0;
		Type resultType = null;
		Type rt = PrimitiveType.RealType;
		Type it = PrimitiveType.IntegerType;
		while (lex.match("*") || lex.match("/") || lex.match("%")) {
			lex.nextLex();
			right = term(sym);

			if(op.equals("*")) {
				if(right.type == rt || result.type == rt) {
					convertToReal(result);
					convertToReal(right);
					resultType = rt;
				} else {
					resultType = it;
				}
				nodeType = BinaryNode.times;
			} else if(op.equals("/")) {
				if(right.type == rt || result.type == it) {
					convertToReal(result);
					convertToReal(right);
					resultType = rt;
				} else {
					resultType = it;
				}
				nodeType = BinaryNode.divide;
			} else if(op.equals("%")) {
				if(right.type != it || result.type != it)
					parseError(41);
				resultType = it;
				nodeType = BinaryNode.remainder;
			}
			if((right.type != it || right.type != rt) ||
					((result.type != it || result.type != rt))) {
				parseError(46);
			}
			if(right.type != result.type) {
				parseError(44);
			}
			result = new BinaryNode(nodeType, resultType, result, right);
		}
		stop("timesExpression");
		return result;
	}

	private Ast term (SymbolTable sym) throws ParseException {
		start("term");
		Ast result = null;
		Type resultType = null;

		//TODO: ambiguous directions...i don't think i need to check anything for ()
		if (lex.match("(")) {
			lex.nextLex();
			result = expression(sym);
			if (! lex.match(")"))
				parseError(22);
			lex.nextLex();
		} else if (lex.match("not")) {
			lex.nextLex();
			result = term(sym);
			if(result.type != PrimitiveType.BooleanType)
				parseError(43);
			result = new UnaryNode(UnaryNode.notOp, PrimitiveType.BooleanType, result);
		} else if (lex.match("new")) {
			lex.nextLex();
			result = type(sym);
			//TODO: is this right?
			result = new UnaryNode(UnaryNode.newOp, PrimitiveType.AddressType, new IntegerNode(result.size()));
		} else if (lex.match("-")) {
			lex.nextLex();
			result = term(sym);
			if(result.type == PrimitiveType.IntegerType) {
			   resultType = PrimitiveType.IntegerType;
			} else if(result.type != PrimitiveType.RealType) {
				resultType = PrimitiveType.RealType;
			} else {
				parseError(46);
			}
			result = new UnaryNode(UnaryNode.negation, resultType, result);
		} else if (lex.match("&")) {
			lex.nextLex();
			result = reference(sym);
			result.type = new PointerType(result.type.baseType); //TODO: is this right?
		} else if (lex.tokenCategory() == lex.intToken) {
			lex.nextLex();
		} else if (lex.tokenCategory() == lex.realToken) {
			lex.nextLex();
		} else if (lex.tokenCategory() == lex.stringToken) {
			lex.nextLex();
		} else if (lex.isIdentifier()) {
			Ast val = reference(sym);
			if (lex.match("(")) {
				lex.nextLex();
				parameterList(sym);
				if (! lex.match(")"))
					parseError(22);
				lex.nextLex();
				}
		} else
			parseError(33);
		stop("term");
		return null;
	}

	private Type addressBaseType(Type t) throws ParseException {
		if (! (t instanceof AddressType))
			parseError(37);
		AddressType at = (AddressType) t;
		return at.baseType;
	}

	private Ast reference (SymbolTable sym) throws ParseException {
		start("reference");
		Ast result = null;
		if (! lex.isIdentifier())
			parseError(27);
		result = sym.lookupName(new FramePointer(), lex.tokenText());
		lex.nextLex();
		while (lex.match("^") || lex.match(".") || lex.match("[")) {
			if (lex.match("^")) {
				Type b = addressBaseType(result.type);
				if ( !(b instanceof PointerType) )
					parseError(38);
				PointerType pb = (PointerType) b;
				result = new UnaryNode(UnaryNode.dereference,
					new AddressType(pb.baseType), result);
				lex.nextLex();
				}
			else if (lex.match(".")) {
				lex.nextLex();
				if (! lex.isIdentifier())
					parseError(27);
				Type b = addressBaseType(result.type);
				if ( !(b instanceof ClassType) )
					parseError(39);
				ClassType pb = (ClassType) b;
				if (! pb.symbolTable.nameDefined(lex.tokenText()))
				   throw new ParseException(29);
				result = pb.symbolTable.lookupName(result, lex.tokenText());
				lex.nextLex();
				}
			else {
				lex.nextLex();
				Ast indexExpression = expression(sym);
				Type b = addressBaseType(result.type);
				if ( !(b instanceof ArrayType) )
					parseError(40);
				ArrayType at = (ArrayType) b;
				if (! indexExpression.type.equals(
					PrimitiveType.IntegerType))
						parseError(41);
				indexExpression = new BinaryNode(
					BinaryNode.minus, 
					PrimitiveType.IntegerType,
					indexExpression, 
						new IntegerNode(at.lowerBound));
				indexExpression = new BinaryNode(
					BinaryNode.times, 
					PrimitiveType.IntegerType,
					indexExpression, 
						new IntegerNode(at.elementType.size()));
				result = new BinaryNode(
					BinaryNode.plus, 
					new AddressType(at.elementType),
					result,
					indexExpression);
				if (! lex.match("]"))
					parseError(24);
				lex.nextLex();
				}
			}
		stop("reference");
		return result;
		}

}
