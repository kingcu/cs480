class BinaryNode extends Ast {
	static final int plus = 1;
	static final int minus = 2;
	static final int times = 3;
	static final int divide = 4;
	static final int and = 5;
	static final int or = 6;
	static final int less = 7;
	static final int lessEqual = 8;
	static final int equal = 9;
	static final int notEqual = 10;
	static final int greater = 11;
	static final int greaterEqual = 12;
	static final int leftShift = 13;
	static final int remainder = 14;

	public BinaryNode (int nt, Type t, Ast l, Ast r) { 
		super(t); 
		NodeType = nt;
		LeftChild = l;
		RightChild = r;
		}

	public String toString() { return "Binary Node " + NodeType +
		"(" + LeftChild + "," + RightChild + ")" + type; }

	public void genCode () {
		LeftChild.genCode();
		RightChild.genCode();
		switch (NodeType) {
			case plus: 
				System.out.println("do addition " + type); break;
			case minus: 
				System.out.println("do subtraction " + type); break;
			case leftShift: 
				System.out.println("do left shift " + type); break;
			case times: 
				System.out.println("do multiplication " + type); break;
			case divide: 
				System.out.println("do division " + type); break;
			case remainder:
				System.out.println("do remainder " + type); break;
			case and: 
				System.out.println("do and " + type); break;
			case or: 
				System.out.println("do or " + type); break;
			case less: 
				System.out.println("compare less " + type); break;
			case lessEqual: 
				System.out.println("compare less or equal" + type); break;
			case equal: 
				System.out.println("compare equal " + type); break;
			case notEqual: 
				System.out.println("compare notEqual " + type); break;
			case greater: 
				System.out.println("compare greater " + type); break;
			case greaterEqual: 
				System.out.println("compare greaterEqual " + type); break;
			}
		}

	public int NodeType;
	public Ast LeftChild;
	public Ast RightChild;
}
