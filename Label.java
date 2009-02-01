//
//	code generation classes
//	written by Tim Budd, spring 2000
//

import java.util.Vector;

class Label {
	static int number = 0;
	public int n;

	Label () { n = ++number; }

	public String toString() { return ".L" + n; }

	void genCode () { System.out.println(toString()+":"); }

	void genBranch () { genBranch("branch to"); }

	void genBranch (String cond) { 
		CodeGen.gen(cond, toString());
	}
}

