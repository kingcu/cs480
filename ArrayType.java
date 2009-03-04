class ArrayType extends Type {
	public final int lowerBound;
	public final int upperBound;
	public final Type elementType;

	public ArrayType (int lb, int ub, Type bt)
		{ lowerBound = lb; upperBound = ub; elementType = bt; }

	public int size ( )
		{ return ((upperBound - lowerBound) + 1) * elementType.size(); }
		
	public String toString() {
		return "Array " + lowerBound + " to " + upperBound + 
			" of " + elementType;
		}

	public boolean equals (Object t) {
		if (! (t instanceof ArrayType))
			return false;
		ArrayType pt = (ArrayType) t;
		if ((pt.lowerBound != lowerBound) || (pt.upperBound != upperBound))
			return false;
		return elementType.equals(pt.elementType);
		}

}
