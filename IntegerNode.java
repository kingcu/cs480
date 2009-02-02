
class IntegerNode extends Ast {
	public int val;

	public IntegerNode (int v) 
		{ super(PrimitiveType.IntegerType); val = v; }
	public IntegerNode (Integer v) 
		{ super(PrimitiveType.IntegerType); val = v.intValue(); }

	public String toString() { return "Integer " + val; }

	public void genCode() {
		System.out.println("Integer " + val);
		}
}
