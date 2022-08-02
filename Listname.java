package norn;

import java.util.*;


public class Listname implements ListExpression {

    private final String listname;
    
    // Abstraction function
    //    AF(listname): an expression representing the email list with the given name (empty by default)
    // Rep invariant
    //    listname is a nonempty case-insensitive string that might contain letters, digits,
    //    underscores, dashes, and periods
    // Safety from rep exposure
    //    all fields are immutable and final
    // Thread Safety argument
    //    all fields immutable and final
    
    /**
     * Constructor for a Listname object.
     * @param listname non-empty string - name of the mailing list
     */
    public Listname(String listname) {
        this.listname = listname.toLowerCase();
        checkRep();
    }
    
    private void checkRep() {
        assert this.listname.length() > 0;
    }
    
    @Override
    public String toString() {
        return listname;
    }
    
    @Override
    public boolean equals(Object that) {
        this.checkRep();
        return that instanceof Listname && this.sameListname((Listname) that);
    }
    
    private boolean sameListname(Listname that) {
        this.checkRep();
        return this.listname().equals(that.listname());
    }
    
    @Override
    public int hashCode() {
        return listname.hashCode();
    }
    
    /**
     * Get the listname
     * @return listname
     */
    public String listname() {
        return listname;
    }
    
    @Override
    public Set<Email> recipients(Map<String, ListExpression> definedLists, Set<Listname> listnamesUsed){
        this.checkRep();
        if (listnamesUsed.contains(this)) {
            throw new IllegalArgumentException("Mutually-recursive list definitions");
        }
        Set<Email> recips = new HashSet<Email>();
        
        if (definedLists.containsKey(this.listname)) {
            Set<Email> emails = definedLists.get(this.listname).recipients(definedLists, listnamesUsed);
            for (Email email : emails) {
                recips.add(email);
            }
        }
        
        return recips;
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
