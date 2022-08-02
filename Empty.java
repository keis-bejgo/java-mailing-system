package norn;

import java.util.*;


public class Empty implements ListExpression {
    
    // Abstraction function
    //    AF(): an empty list expression
    // Rep invariant
    //    true
    // Safety from rep exposure
    //    no fields, no exposure
    // Thread Safety argument
    //    all fields immutable and final

    private static final int HASH = 42;
    
    /**
     * Constructor for an Empty object.
     */
    public Empty() {
    }
    
    private void checkRep() {
        assert this != null;
    }
    
    @Override
    public String toString() {
        this.checkRep();
        return "";
    }
    
    @Override
    public boolean equals(Object that) {
        this.checkRep();
        return that instanceof Empty;
    }
    
    @Override
    public int hashCode() {
        this.checkRep();
        return HASH;
    }
    
    @Override
    public Set<Email> recipients(Map<String, ListExpression> definedLists, Set<Listname> listnamesUsed){
        this.checkRep();
        return Set.of();
    }
    
    @Override
    public Map<String, ListExpression> definitions(){
        this.checkRep();
        return Map.of();
    }
    
    @Override
    public List<ListExpression> visualizeExpressions(){
        List<ListExpression> expressions = new ArrayList<ListExpression>();
        expressions.add(new Empty());
        expressions.add(new Empty());
        return expressions;
    }
}
