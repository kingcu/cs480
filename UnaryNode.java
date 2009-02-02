
class UnaryNode extends Ast {
	static final int dereference = 1;
	static final int convertToReal = 2;
	static final int notOp = 3;
	static final int negation = 4;


	public UnaryNode (int nt, Type t, Ast b) { 
		super(t); 
		nodeType = nt;
		child = b;
	}

	public int nodeType;
	public Ast child;

	public String toString() { return "Unary node " + nodeType +
		"(" + child + ")" + type; }

	public void genCode () {
		switch(nodeType) {
			case dereference:
				child.genCode();
				System.out.println("dereference " + type); 
				break;
			case convertToReal:
				child.genCode();
				System.out.println("convert to real" + type); 
				break;
			case notOp:
				child.genCode();
				System.out.println("not op " + type); 
				break;
			case negation:
				child.genCode();
				System.out.println("numeric negation " + type);
				break;
		}
	}

	public void branchIfTrue (Label lab) throws ParseException {
		genCode();
		System.out.println("Branch if True " + lab);
	}

	public void branchIfFalse (Label lab) throws ParseException { 
		genCode();
		System.out.println("Branch if False " + lab);
	}
}
