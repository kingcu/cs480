//
//	written (and rewritten) by Tim Budd
//

import java.util.Hashtable;

class GlobalSymbolTable implements SymbolTable {
	private Hashtable table = new Hashtable(); //let it grow automatically

	public void enterConstant(String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType(String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable(String name, Type type)
		{ enterSymbol (new GlobalSymbol(name, new AddressType(type), name)); }

	public void enterFunction(String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	private void enterSymbol(Symbol s) {
		table.put(s.name, s);
	}

	private Symbol findSymbol(String name) {
		Symbol s = (Symbol)table.get(name);
		if(s != null) {
			return s;
		} else {
			return null;
		}
	}

	public boolean nameDefined(String name) {
		Symbol s = findSymbol(name);
		if (s != null) return true;
		else return false;
	}

	//TODO: i modified from original to meet ParseExceptions thrown specified in
	//assignment description...not sure if this was necessary?
	public Type lookupType(String name) throws ParseException {
		Symbol s = findSymbol(name);
		if(s != null) {
		   if(s instanceof TypeSymbol) {
				TypeSymbol ts = (TypeSymbol) s;
				return ts.type;
		   } else {
			   throw new ParseException(30);
		   }
		} else {
			throw new ParseException(42, name);
		}
	}

	public Ast lookupName(Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if (s == null)
			throw new ParseException(42, name);
		// now have a valid symbol
		if (s instanceof GlobalSymbol) {
			GlobalSymbol gs = (GlobalSymbol) s;
			return new GlobalNode(gs.type, name);
		}
		if (s instanceof ConstantSymbol) {
			ConstantSymbol cs = (ConstantSymbol) s;
			return cs.value;
		}
		return null; // should never happen
	}

	public int size() {
		return 0;
	}
}
