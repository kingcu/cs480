//
//	abstract syntax tree
//

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

    public Ast optimize() {return this;}

    protected boolean isIntConst() {
        return this instanceof IntegerNode;
    }

    protected int intConst() {
        if(isIntConst()) {
            IntegerNode tmp = (IntegerNode)this;
            return tmp.val;
        }
        return 0;
    }

    //TODO: this can't be right....a new binary node maybe?
    protected BinaryNode additionNode() {
        if(this instanceof BinaryNode) {
            BinaryNode tmp = (BinaryNode)this;
            if(tmp.NodeType == BinaryNode.plus)
                return tmp;
        }
        return null;
    }
}
