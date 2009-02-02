
class FramePointer extends Ast {
	public FramePointer () { super(PrimitiveType.VoidType); }

	public void genCode () {
		System.out.println("frame pointer");
		}

	public String toString() { return "frame pointer"; }
}

