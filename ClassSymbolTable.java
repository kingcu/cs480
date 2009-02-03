//
//	written (and rewritten) by Tim Budd
//

import java.util.Hashtable;

class ClassSymbolTable implements SymbolTable {
	private SymbolTable surround = null;
	private Hashtable table = new Hashtable(); //let it grow automatically
	private int offsetCount = 0;

	ClassSymbolTable (SymbolTable s) { surround = s; }

	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type) {
		enterSymbol(new OffsetSymbol(name, new AddressType(type), offsetCount));
		offsetCount += type.size();
	}

	public void enterFunction (String name, FunctionType ft) 
		// this should really be different as well,
		// but we will leave alone for now
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

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

	//TODO: might need to implement exceptions, as defined in assignment...
	public Type lookupType (String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof TypeSymbol)) {
			TypeSymbol ts = (TypeSymbol) s;
			return ts.type;
		}
		return surround.lookupType(name);
	}

	public Ast lookupName (Ast base, String name) throws ParseException {
		Symbol s = findSymbol(name);
		if (s == null)
			return surround.lookupName(base, name);
		// else we have a symbol here
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
		//TODO: return total size of data fields in class symbol table
		return offsetCount;
	}
}
