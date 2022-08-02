package norn;

import java.util.*;


public class Sequence implements ListExpression {
    
    private final ListExpression first;
    private final ListExpression second;
    
    // Abstraction function
    //    AF(first, second): represents the sequence of two ListExpressions 
    // Rep invariant
    //     first != null
    //     second != null
    // Safety from rep exposure
    //    all fields are immutable and final
    // Thread Safety argument
    //    all fields immutable and final
    
    /**
     * Constructor for Union object.
     * @param first the first ListExpression in this union
     * @param second the second ListExpression in this union
     */
    public Sequence(ListExpression first, ListExpression second) {
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
        return this.first.toString() + "; " + this.second.toString();
    }
    
    @Override
    public boolean equals(Object that) {
        this.checkRep();
        return that instanceof Sequence && this.sameExpression((Sequence) that);
    }
    
    private boolean sameExpression(Sequence that) {
        this.checkRep();
        return this.first().equals(that.first()) && this.second().equals(that.second());
    }
    
    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }
    
    /**
     * Getter method for getting the first ListExpression of this Sequence.
     * @return the first ListExpression
     *
     */
    public ListExpression first() {
        return first;
    }
    
    /**
     * Getter method for getting the second ListExpression of this Sequence.
     * @return the second ListExpression
     */
    public ListExpression second() {
        return second;
    }
    
    @Override
    public Set<Email> recipients(Map<String, ListExpression> definedLists, Set<Listname> listnamesUsed){
        this.checkRep();
        //get defs from first expr, put definedList's exprs into it but don't overwrite first exprs        
        Map<String,ListExpression> origDefs = this.first.definitions();    
        Map<String,ListExpression> firstDefs = new HashMap<String,ListExpression>();
        for(String key : origDefs.keySet()) {
            firstDefs.put(key, origDefs.get(key));
        }           
        
        for(String key : definedLists.keySet()) {
            if(!firstDefs.containsKey(key)) {
                firstDefs.put(key, definedLists.get(key));
            }
        }
                
        return this.second.recipients(firstDefs, listnamesUsed);
    }
    
    @Override
    public Map<String, ListExpression> definitions(){
        this.checkRep();
        Map<String,ListExpression> origDefs = this.first.definitions();    
        Map<String,ListExpression> defs = new HashMap<String,ListExpression>();
        for(String key : origDefs.keySet()) { //put defs from first expr into defs
            defs.put(key, origDefs.get(key));
        }           
        
        Map<String,ListExpression> secondDefs = this.second.definitions();
        for(String key : secondDefs.keySet()) { //add in defs from second expr, overwriting first expr's if needed
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
