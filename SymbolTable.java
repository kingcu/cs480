//
//	written (and rewritten) by Tim Budd
//

interface SymbolTable {
		// methods to enter values into symbol table
	public void enterConstant (String name, Ast value);
	public void enterType (String name, Type type);
	public void enterVariable (String name, Type type);
	public void enterFunction (String name, FunctionType ft);
	public int size();

		// methods to search the symbol table
	public boolean nameDefined (String name);
	public Type lookupType (String name) throws ParseException;
	public Ast lookupName (Ast base, String name) throws ParseException;
}
