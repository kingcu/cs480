//
//	code generation classes
//	written by Tim Budd, spring 2000
//

class Label {
    static int number = 0;
    public int n;

    Label () { n = ++number; }

    public String toString() { return "Label " + n; }

    void genCode () { System.out.println(".L"+n+":"); }

    void genBranch () { System.out.println("branch to L" + n); }

    void genBranch (String cond) {
        System.out.println("\t" + cond + "\t.L" + n); }
}
