class PointerType extends PrimitiveType {
	protected final Type baseType;

	public PointerType (Type t) { super(4); baseType = t; }

	public boolean equals (Object t) {
		if (! (t instanceof PointerType))
			return false;
		PointerType pt = (PointerType) t;
		return baseType.equals(pt.baseType);
		}

	public String toString() { return "Pointer to " + baseType.toString(); }
}
