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

    public Ast optimize() {
        Ast tmpAst;
        Ast lc = LeftChild.optimize();
        Ast rc = RightChild.optimize();
        BinaryNode lca;
        BinaryNode rca;

        //PLUS SECTION
        if(NodeType == BinaryNode.plus) {
            if(lc.isIntConst() && !rc.isIntConst()) {
                tmpAst = lc;
                lc = rc;
                rc = tmpAst;
            }
            if(lc.isIntConst() && rc.isIntConst()) {
                return new IntegerNode(lc.intConst() + rc.intConst());
            }

            if(rc.isIntConst() && rc.intConst() == 0) {
                lc.type = type;
                return lc;
            }

            lca = lc.additionNode();
            if(lca != null) {
                if(rc.isIntConst() && lca.RightChild.isIntConst()) {
                    rc = new IntegerNode(lca.RightChild.intConst() + rc.intConst());
                    lc = lca.LeftChild;
                    return new BinaryNode(NodeType, type, lc, rc).optimize();
                }
                if(!rc.isIntConst()) {
                    tmpAst = lca.RightChild;
                    lc = new BinaryNode(BinaryNode.plus, type, lca.LeftChild, RightChild);
                    rc = tmpAst;
                    return new BinaryNode(NodeType, type, lc, rc).optimize();
                }
            }

            rca = rc.additionNode();
            if(rca != null) {
                if(rca.RightChild.isIntConst()) {
                    tmpAst = lc;
                    lc = new BinaryNode(BinaryNode.plus, type, lc, rca.LeftChild);
                    rc = rca.RightChild;
                    new BinaryNode(NodeType, type, lc, rc).optimize();
                }
            }
        //MINUS SECTION
        } else if(this.NodeType == BinaryNode.minus) {
            if(rc.isIntConst()) {
                return new BinaryNode(BinaryNode.plus, type, lc, 
                        new IntegerNode(rc.intConst() * -1)).optimize();
            }
        //TIMES SECTION
        } else if(this.NodeType == BinaryNode.times) {
            if(lc.isIntConst() && !rc.isIntConst()) {
                tmpAst = lc;
                lc = rc;
                rc = tmpAst;
            }
            if(lc.isIntConst() && rc.isIntConst()) {
                return new IntegerNode(lc.intConst() * rc.intConst());
            } 

            if(lc.isIntConst() && lc.intConst() == 0 || 
                    rc.isIntConst() && rc.intConst() == 0) {
                return new IntegerNode(0);
            }
            if(rc.isIntConst() && rc.intConst() == 1) {
                return lc; //TODO: do we need to make a new node?
            }

            lca = LeftChild.additionNode();
            if(lca != null && lca.RightChild.isIntConst() && rc.isIntConst()) {
                return new BinaryNode(BinaryNode.plus, type, 
                        new BinaryNode(BinaryNode.times, type, lca.LeftChild, rc),
                        new BinaryNode(BinaryNode.times, type, lca.RightChild, rc)).optimize();
            }
        }

        return new BinaryNode(NodeType, type, lc, rc);
    }
}
