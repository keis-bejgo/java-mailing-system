package norn;

import java.util.*;


public class Union implements ListExpression {
    
    private final ListExpression first;
    private final ListExpression second;
    
    // Abstraction function
    //    AF(first, second): represents the union of two ListExpressions (i.e. the elements in the first ListExpression
    //          and all of the elements in the second ListExpression)
    // Rep invariant
    //     first != null
    //     second != null
    // Safety from rep exposure
    //    all fields are immutable and final
    //Thread Safety argument
    //    all fields immutable and final
    
    /**
     * Constructor for Union object.
     * @param first the first ListExpression in this union
     * @param second the second ListExpression in this union
     */
    public Union(ListExpression first, ListExpression second) {
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
        return "(" + this.first.toString() + ", " + this.second.toString() + ")";
    }
    
    @Override
    public boolean equals(Object that) {
        this.checkRep();
        return that instanceof Union && this.sameExpression((Union) that);
    }
    
    private boolean sameExpression(Union that) {
        this.checkRep();
        return (this.first().equals(that.first()) && this.second().equals(that.second()))
                || (this.first().equals(that.second()) && this.second().equals(that.first()));
    }
    
    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }
    
    /**
     * Getter method for getting the first ListExpression of this Union.
     * @return the first ListExpression
     *
     */
    public ListExpression first() {
        return first;
    }
    
    /**
     * Getter method for getting the second ListExpression of this Union.
     * @return the second ListExpression
     */
    public ListExpression second() {
        return second;
    }
    
    @Override
    public Set<Email> recipients(Map<String, ListExpression> definedLists, Set<Listname> listnamesUsed){
        this.checkRep();
        Set<Email> recip = new HashSet<Email>();
        
        for(Email email : this.first.recipients(definedLists, listnamesUsed)) {
            recip.add(email);
        }
        
        //get defs from first expr, put definedList's exprs into it but don't overwrite first exprs
        Map<String,ListExpression> origDefs = this.first.definitions();    
        Map<String,ListExpression> firstDefs = new HashMap<String,ListExpression>();
        for(String key : origDefs.keySet()) { //put first expr's defs into firstDefs
            firstDefs.put(key, origDefs.get(key));
        }
        
        for(String key : definedLists.keySet()) { //put definedLists's defs into firstDefs, don't overwrite
            if(!firstDefs.containsKey(key)) {
                firstDefs.put(key, definedLists.get(key));
            }
        }
                
        //put all of second's recipients into recip using firstDefs as input definedLists 
        for(Email email : this.second.recipients(firstDefs, listnamesUsed)) {
            recip.add(email);
        }
                
        return recip;
    }
    
    @Override
    public Map<String, ListExpression> definitions(){
        this.checkRep();
        Map<String,ListExpression> origDefs = this.first.definitions(); //get defs from first expr
        Map<String,ListExpression> defs = new HashMap<String,ListExpression>();
        for(String key : origDefs.keySet()) {
            defs.put(key, origDefs.get(key));
        }        
        
        Map<String,ListExpression> secondDefs = this.second.definitions();
        for(String key : secondDefs.keySet()) { //get defs from second expr, overwrite when needed
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
