//
//	written (and rewritten) by Tim Budd
//

import java.util.Hashtable;
import java.util.ArrayList;

class FunctionSymbolTable implements SymbolTable {
	private Hashtable table = new Hashtable(); //let it grow automatically
	private ArrayList params = new ArrayList(2); //initial cap of 2...most of my functions have <= 2
	private int offsetCount = 0;
	private int paramOffsetCount = 8;
	public boolean doingArguments = false;
	SymbolTable surrounding = null;

	FunctionSymbolTable (SymbolTable st) { surrounding = st; }

	public void enterConstant (String name, Ast value) 
		{ enterSymbol(new ConstantSymbol(name, value)); }

	public void enterType (String name, Type type) 
		{ enterSymbol (new TypeSymbol(name, type)); }

	public void enterVariable (String name, Type type)
	{
		//TODO: I believe using negative for regular offsets is right, unless I am confused...
		if(doingArguments == false) {
			enterSymbol(new OffsetSymbol(name, new AddressType(type), -offsetCount));
			offsetCount += type.size();
		} else {
			OffsetSymbol blurg  = new OffsetSymbol(name, new AddressType(type), paramOffsetCount);
			paramOffsetCount += type.size();
			enterSymbol(blurg);
			params.add(blurg);
		}
	}

	public void enterFunction (String name, FunctionType ft) 
		{ enterSymbol (new GlobalSymbol(name, ft, name)); }

	public void enteringParameters(boolean flag) {
		doingArguments = (flag == true);
	}

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

	//TODO: implement exceptions like defined in assignment?
	public Type lookupType (String name) throws ParseException {
		Symbol s = findSymbol(name);
		if ((s != null) && (s instanceof TypeSymbol)) {
			TypeSymbol ts = (TypeSymbol) s;
			return ts.type;
		}
		// note how we check the surrounding scopes
		return surrounding.lookupType(name);
	}

	//TODO: might want to implement some error handling...
	public Type parameterType(int index) {
		OffsetSymbol blech = (OffsetSymbol)params.get(index);
		return blech.type;
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
		return offsetCount;
	} 
}
