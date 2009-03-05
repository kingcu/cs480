class UnaryNode extends Ast {
	static final int dereference = 1;
	static final int convertToReal = 2;
	static final int notOp = 3;
	static final int negation = 4;
	static final int newOp = 5;


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
		child.genCode();
		switch(nodeType) {
			case dereference:
				System.out.println("dereference " + type); break;
			case convertToReal:
				System.out.println("convert to real" + type); break;
			case notOp:
				System.out.println("not op " + type); break;
			case negation:
				System.out.println("numeric negation " + type); break;
			case newOp:
				System.out.println("new memory " + type); break;
		}
	}

    public Ast optimize() {
        Ast tmpChild = child.optimize();
        if(nodeType == UnaryNode.negation && tmpChild.isIntConst()) {
            return new IntegerNode(tmpChild.intConst() * -1);
        } else {
            return new UnaryNode(nodeType, type, tmpChild);
        }
    }
}
