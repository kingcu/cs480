class FunctionSymbolTable implements SymbolTable {
	SymbolTable surrounding = null;

	FunctionSymbolTable (SymbolTable st) { surrounding = st; }

	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	private int paramOffset = 8;
	private int localOffset = 0;
	public void enterIdentifier (String name, Type type)
	{
		if (doingArguments) {
			enterSymbol(new OffsetSymbol(name, new AddressType(type), paramOffset));
			paramOffset += type.size();
		}
		else {
			localOffset += type.size();
			enterSymbol(new OffsetSymbol(name, new AddressType(type), - localOffset));
		}
	}

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	public boolean doingArguments = true;
	private SymbolLink firstLink = null;
	private class SymbolLink {
		public Symbol sym;
		public SymbolLink link;
		public SymbolLink (Symbol s, SymbolLink l) 
			{ sym = s; link = l; }
	}
	private void enterSymbol (Symbol s) {
		firstLink = new SymbolLink(s, firstLink);
	}

	private Symbol findSymbol (String name) {
		for (SymbolLink l = firstLink; l != null; l = l.link)
			if (l.sym.name.equals(name))
				return l.sym;
		return null;
	}

	public boolean nameDefined (String name) {
		Symbol s = findSymbol(name);
		if (s != null) return true;
		else return false;
	}

	public Type lookupType (String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof TypeSymbol)) {
			TypeSymbol ts = (TypeSymbol) s;
			return ts.type;
			}
		return surrounding.lookupType(name);
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof GlobalSymbol)) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if ((s != null) && (s instanceof OffsetSymbol)) {
			OffsetSymbol os = (OffsetSymbol) s;
			return new BinaryNode(BinaryNode.plus, os.type,
				base, new IntegerNode(os.location));
			}
		if ((s != null) && (s instanceof ConstantSymbol)) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		return surrounding.lookupName(base, name);
	}

	public int size () { return localOffset; }
}
