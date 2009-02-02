//
//	abstract syntax tree
//

import java.util.Vector;

abstract class Ast {
	public Ast(Type t) { type = t; }

	public Type type;

	abstract public void genCode ();

	public void branchIfTrue (Label lab) throws ParseException {
		genCode();
		System.out.println("Branch if True " + lab);
	}

	public void branchIfFalse (Label lab) throws ParseException { 
		genCode();
		System.out.println("Branch if False " + lab);
	}
}
