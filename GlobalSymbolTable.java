class GlobalSymbolTable implements SymbolTable {
	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterIdentifier (String name, Type type)
		{ enterSymbol (new GlobalSymbol(name, new AddressType(type), name)); }

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

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
		throw new ParseException(30);
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof GlobalSymbol)) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
			}
		if ((s != null) && (s instanceof ConstantSymbol)) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
			}
		throw new ParseException(42, name);
	}

	public int size () { return 0; }
}

