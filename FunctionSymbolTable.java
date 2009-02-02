//
//	written (and rewritten) by Tim Budd
//

import java.util.Hashtable;

class FunctionSymbolTable implements SymbolTable {
	private Hashtable table = new Hashtable(); //let it grow automatically
	SymbolTable surrounding = null;

	FunctionSymbolTable (SymbolTable st) { surrounding = st; }

	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type)
	{
		// TODO: this is for you to figure out.
		// I'll leave a stub, which you should
		// replace with the real thing
                if (!nameDefined(name))
		    enterSymbol(new OffsetSymbol(name, new AddressType(type), 27));
	}

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	public void enteringParameters(boolean flag) {
	}


	public boolean doingArguments = true;

	private void enterSymbol (Symbol s) {
		table.put(s.name, s);
	}

	private Symbol findSymbol (String name) {
		Symbol s = (Symbol)table.get(name);
		if(s != null)
			return s;
		else
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
		// note how we check the surrounding scopes
		return surrounding.lookupType(name);
	}

	public Type parameterType(int index) {
		//TODO: implement!
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if (s == null)
			return surrounding.lookupName(base, name);
		// we have a symbol here
		if (s instanceof GlobalSymbol) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
		}
		if (s instanceof OffsetSymbol) {
			OffsetSymbol os = (OffsetSymbol) s;
			return new BinaryNode(BinaryNode.plus, os.type,
				base, new IntegerNode(os.location));
		}
		if (s instanceof ConstantSymbol) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
		}
		return null; // should never happen
	}

	public int size() {
		//TODO: implement size as according to assignment specification
                return table.size();
	} 
}
