package norn;

import java.util.*;


public class Difference  implements ListExpression {

    private final ListExpression first;
    private final ListExpression second;
    
    // Abstraction function
    //    AF(first,second): represents the difference between two ListExpressions (i.e. the elements that are in the 
    //          first ListExpression but not in the second ListExpression)
    // Rep invariant
    //     first != null
    //     second != null
    // Safety from rep exposure
    //    all fields are immutable and final
    //Thread Safety argument
    //    all fields immutable and final
    
    /**
     * Constructor for a Difference object.
     * @param first the first ListExpresion
     * @param second the second ListExpression
     */
    public Difference(ListExpression first, ListExpression second) {
        this.first = first;
        this.second = second;
        checkRep();
    }
    
    private void checkRep() {
        assert this.first != null;
        assert this.second != null;
    }
    
    @Override
    public String toString() {
        this.checkRep();
        return "(" + this.first.toString() + " ! " + this.second.toString() + ")";
    }
    
    @Override
    public boolean equals(Object that) {
        this.checkRep();
        return that instanceof Difference && this.sameExpression((Difference) that);
    }
    
    private boolean sameExpression(Difference that) {
        this.checkRep();
        return this.first().equals(that.first()) && this.second().equals(that.second());
    }
    
    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }
    
    /**
     * Getter method for getting the first ListExpression in this Difference object.
     * @return the first ListExpression
     */
    public ListExpression first() {
        return first;
    }
    
    /**
     * Getter method for getting the second ListExpression in this Difference object.
     * @return the second ListExpression
     */
    public ListExpression second() {
        return second;
    }
    
    @Override
    public Set<Email> recipients(Map<String, ListExpression> definedLists, Set<Listname> listnamesUsed){
        this.checkRep();
        Set<Email> firstRecip = this.first.recipients(definedLists, listnamesUsed);
        
        //get defs from first expr, add definedLists defs (but don't overwrite first defs)
        Map<String,ListExpression> origDefs = this.first.definitions();    
        Map<String,ListExpression> firstDefs = new HashMap<String,ListExpression>();
        for(String key : origDefs.keySet()) { //get defs from first expr's definitions
            firstDefs.put(key, origDefs.get(key));
        }           
               
        for(String key : definedLists.keySet()) { //put defs from definedLists into firstDefs,dont overwrite first expr
            if(!firstDefs.containsKey(key)) {
                firstDefs.put(key, definedLists.get(key));
            }
        }
        
        Set<Email> secondRecip = this.second.recipients(firstDefs, listnamesUsed);
        Set<Email> recip = new HashSet<Email>(); //put difference of first and second exprs into recips
        for(Email email : firstRecip) {
            if (!secondRecip.contains(email)) {
                recip.add(email);
            }
        }
               
        return recip;
    }
    
    @Override
    public Map<String, ListExpression> definitions(){
        this.checkRep();
        Map<String,ListExpression> origDefs = this.first.definitions();    
        Map<String,ListExpression> defs = new HashMap<String,ListExpression>();
        for(String key : origDefs.keySet()) {
            defs.put(key, origDefs.get(key));
        }           
        
        Map<String,ListExpression> secondDefs = this.second.definitions();
        for(String key : secondDefs.keySet()) {
            defs.put(key, secondDefs.get(key));
        }
       
        return defs;    
    }
    
    @Override
    public List<ListExpression> visualizeExpressions(){
        List<ListExpression> expressions = new ArrayList<ListExpression>();
        expressions.add(first());
        expressions.add(second());
        return expressions;
    }
}
