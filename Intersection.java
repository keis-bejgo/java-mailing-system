package norn;

import java.util.*;


public class Intersection implements ListExpression {
    
    private final ListExpression first;
    private final ListExpression second;
    
    // Abstraction function
    //    AF(first, second): represents the intersection of two ListExpressions (i.e. the elements that are in the first
    //          ListExpression and in the second ListExpression)
    // Rep invariant
    //     first != null
    //     second != null
    // Safety from rep exposure
    //    all fields are immutable and final
    //Thread Safety argument
    //    all fields immutable and final
    
    /**
     * Constructor for an Intersection object.
     * @param first the first ListExpression
     * @param second the second ListExpression
     */
    public Intersection(ListExpression first, ListExpression second) {
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
        return "(" + this.first.toString() + " * " + this.second.toString() + ")";
    }
    
    @Override
    public boolean equals(Object that) {
        this.checkRep();
        return that instanceof Intersection && this.sameExpression((Intersection) that);
    }
    
    private boolean sameExpression(Intersection that) {
        this.checkRep();
        return (this.first().equals(that.first()) && this.second().equals(that.second())
                || (this.second.equals(that.first())&& this.first().equals(that.second())));
    }
    
    @Override
    public int hashCode() {
        return this.first.hashCode() + this.second.hashCode();
    }
    
    /**
     * getter to get the first ListExpression in this intersection
     * @return the first ListExpression in this Intersection object
     */
    public ListExpression first() {
        return this.first;
    }
    
    /**
     * getter to get the second ListExpression in this intersection
     * @return the second ListExpression in this Intersection object
     */
    public ListExpression second() {
        return this.second;
    }
    
    @Override
    public Set<Email> recipients(Map<String, ListExpression> definedLists, Set<Listname> listnamesUsed){
        this.checkRep();
        Set<Email> firstRecip = this.first.recipients(definedLists, listnamesUsed);
                
        //get defs from first expr, add definedLists defs (but don't overwrite first defs)
        Map<String,ListExpression> origDefs = this.first.definitions();    
        Map<String,ListExpression> firstDefs = new HashMap<String,ListExpression>();
        for(String key : origDefs.keySet()) { //put defs from first expr into firstDefs
            firstDefs.put(key, origDefs.get(key));
        }        
               
        for(String key : definedLists.keySet()) { //put defs from definedLists into firstDefs
            if(!firstDefs.containsKey(key)) {
                firstDefs.put(key, definedLists.get(key));
            }
        }
        
        Set<Email> secondRecip = this.second.recipients(firstDefs, listnamesUsed); //recips using firstExpr + definedLists
        Set<Email> recip = new HashSet<Email>(); 
        for(Email email : firstRecip) { //put intersection of first and second exprs's recips together
            if (secondRecip.contains(email)) {
                recip.add(email);
            }
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
        for(String key : secondDefs.keySet()) { //add defs from second expr, overwrite when needed
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
