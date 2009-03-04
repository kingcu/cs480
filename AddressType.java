class AddressType extends PrimitiveType {
	protected final Type baseType;

	public AddressType (Type t) { super(4); baseType = t; }

	public boolean equals (Object t) {
		if (! (t instanceof AddressType))
			return false;
		AddressType pt = (AddressType) t;
		return baseType.equals(pt.baseType);
		}

	public String toString() { return "Address to " + baseType.toString(); }
}
